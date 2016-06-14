package com.worldventures.dreamtrips.core.rx.goro;

import android.content.Context;

import com.stanfy.enroscar.goro.Goro;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import rx.Scheduler;
import rx.Subscription;
import rx.functions.Action0;
import rx.internal.schedulers.GenericScheduledExecutorService;
import rx.internal.schedulers.ScheduledAction;
import rx.plugins.RxJavaPlugins;
import rx.subscriptions.CompositeSubscription;
import rx.subscriptions.MultipleAssignmentSubscription;
import rx.subscriptions.Subscriptions;

public class GoroScheduler extends Scheduler {

    private final Goro goro;

    public GoroScheduler(Context context) {
        this(Goro.bindOnDemandWith(context.getApplicationContext()));
    }

    public GoroScheduler(Goro goro) {
        this.goro = goro;
    }

    @Override
    public Worker createWorker() {
        return new GoroSchedulerWorker(goro);
    }

    private final static class GoroSchedulerWorker extends Worker implements Callable<Object> {

        private final CompositeSubscription tasks;
        private final ConcurrentLinkedQueue<ScheduledAction> queue;
        private final AtomicInteger wip;
        private final Goro goro;

        public GoroSchedulerWorker(Goro goro) {
            this.goro = goro;
            this.queue = new ConcurrentLinkedQueue<ScheduledAction>();
            this.wip = new AtomicInteger();
            this.tasks = new CompositeSubscription();
        }

        @Override
        public Subscription schedule(Action0 action) {
            if (this.isUnsubscribed()) {
                return Subscriptions.unsubscribed();
            } else {
                ScheduledAction scheduledAction = new ScheduledAction(action, this.tasks);
                this.tasks.add(scheduledAction);
                this.queue.offer(scheduledAction);
                if (this.wip.getAndIncrement() == 0) {
                    try {
                        this.goro.schedule(this);
                    } catch (RejectedExecutionException e) {
                        this.tasks.remove(scheduledAction);
                        this.wip.decrementAndGet();
                        RxJavaPlugins.getInstance().getErrorHandler().handleError(e);
                        throw e;
                    }
                }

                return scheduledAction;
            }
        }

        @Override
        public Object call() throws Exception {
            do {
                ScheduledAction scheduledAction = this.queue.poll();
                if (!scheduledAction.isUnsubscribed()) {
                    scheduledAction.run();
                }
            } while (this.wip.decrementAndGet() > 0);
            return null;
        }

        public Subscription schedule(final Action0 action, long delayTime, TimeUnit unit) {
            if (delayTime <= 0L) {
                return this.schedule(action);
            } else if (this.isUnsubscribed()) {
                return Subscriptions.unsubscribed();
            } else {
                MultipleAssignmentSubscription first = new MultipleAssignmentSubscription();
                final MultipleAssignmentSubscription mas = new MultipleAssignmentSubscription();
                mas.set(first);
                this.tasks.add(mas);
                final Subscription removeMas = Subscriptions.create(new Action0() {
                    public void call() {
                        GoroSchedulerWorker.this.tasks.remove(mas);
                    }
                });
                ScheduledAction scheduledAction = new ScheduledAction(new Action0() {
                    public void call() {
                        if (!mas.isUnsubscribed()) {
                            Subscription subscription = GoroSchedulerWorker.this.schedule(action);
                            mas.set(subscription);
                            if (subscription.getClass() == ScheduledAction.class) {
                                ((ScheduledAction) subscription).add(removeMas);
                            }

                        }
                    }
                });
                first.set(scheduledAction);
                try {
                    ScheduledFuture t = GenericScheduledExecutorService.getInstance()
                            .schedule(scheduledAction, delayTime, unit);
                    scheduledAction.add(t);
                    return removeMas;
                } catch (RejectedExecutionException e) {
                    RxJavaPlugins.getInstance().getErrorHandler().handleError(e);
                    throw e;
                }
            }
        }

        public boolean isUnsubscribed() {
            return this.tasks.isUnsubscribed();
        }

        public void unsubscribe() {
            this.tasks.unsubscribe();
        }

    }
}