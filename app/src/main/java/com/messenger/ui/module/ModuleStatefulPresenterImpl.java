package com.messenger.ui.module;

// TODO: 5/10/16 Implement state saving and restoring acccording to activity lifecycle
public abstract class ModuleStatefulPresenterImpl<V extends ModuleView, S> extends ModulePresenterImpl<V>
    implements ModuleStatefulPresenter<V, S> {

    private S state;

    public ModuleStatefulPresenterImpl(V view) {
        super(view);
        this.state = createNewState();
    }

    @Override
    public S getState() {
        return state;
    }

    protected void setState(S state) {
        this.state = state;
    }

    protected abstract S createNewState();
}
