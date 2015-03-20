// Copyright 2014 Miras Absar
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.worldventures.dreamtrips.view.custom;

public class Pointer {

    // This Pointer's id.
    private int mId;

    // The time (in milliseconds) when this Pointer went down.
    private long mDownTime;

    // The x coordinate (in pixels) where this Pointer went down.
    private double mDownX;

    // The y coordinate (in pixels) where this Pointer went down.
    private double mDownY;

    // The time (in milliseconds) when this Pointer went up.
    private long mUpTime;

    // The x coordinate (in pixels) where this Pointer went up.
    private double mUpX;

    // The y coordinate (in pixels) where this Pointer went up.
    private double mUpY;

    // Limits for identifying taps and swipes.
    private double mUpXUpperLimit;
    private double mUpXLowerLimit;
    private double mUpYUpperLimit;
    private double mUpYLowerLimit;

    public Pointer(int pId,
                   long pDownTime,
                   double pDownX, double pDownY,
                   double pMovementLimitPx) {

        mId = pId;
        mDownTime = pDownTime;
        mDownX = pDownX;
        mDownY = pDownY;

        mUpXUpperLimit = mDownX + pMovementLimitPx;
        mUpXLowerLimit = mDownX - pMovementLimitPx;
        mUpYUpperLimit = mDownY + pMovementLimitPx;
        mUpYLowerLimit = mDownY - pMovementLimitPx;
    }

    public void setUpTime(long pUpTime) {
        mUpTime = pUpTime;
    }

    public int getId() {
        return mId;
    }

    public double getDownX() {
        return mDownX;
    }

    public double getDownY() {
        return mDownY;
    }

    public double getUpX() {
        return mUpX;
    }

    public void setUpX(double pUpX) {
        mUpX = pUpX;
    }

    public double getUpY() {
        return mUpY;
    }

    public void setUpY(double pUpY) {
        mUpY = pUpY;
    }

    public boolean existedWithinTimeLimit(int pTimeLimit) {
        return mUpTime - mDownTime <= pTimeLimit;
    }

    public boolean tapped() {
        return mUpX < mUpXUpperLimit &&
                mUpX > mUpXLowerLimit &&
                mUpY < mUpYUpperLimit &&
                mUpY > mUpYLowerLimit;
    }

    public boolean swipedUp() {
        return mUpX < mUpXUpperLimit &&
                mUpX > mUpXLowerLimit &&
                mUpY <= mUpYLowerLimit;
    }

    public boolean swipedDown() {
        return mUpX < mUpXUpperLimit &&
                mUpX > mUpXLowerLimit &&
                mUpY >= mUpYUpperLimit;
    }

    public boolean swipedLeft() {
        return mUpX <= mUpXLowerLimit &&
                mUpY < mUpYUpperLimit &&
                mUpY > mUpYLowerLimit;
    }

    public boolean swipedRight() {
        return mUpX >= mUpXUpperLimit &&
                mUpY < mUpYUpperLimit &&
                mUpY > mUpYLowerLimit;
    }

    private double distanceFormula(double pXI, double pYI,
                                   double pXII, double pYII) {

        return Math.sqrt(Math.pow(pXI - pXII, 2) + Math.pow(pYI - pYII, 2));
    }

    public boolean pinchedIn(Pointer pPointer, double pMovementLimitPx) {
        return distanceFormula(mDownX, mDownY, pPointer.getDownX(), pPointer.getDownY()) + pMovementLimitPx <=
                distanceFormula(mUpX, mUpY, pPointer.getUpX(), pPointer.getUpY());
    }

    public boolean pinchedOut(Pointer pPointer, double pMovementLimitPx) {
        return distanceFormula(mDownX, mDownY, pPointer.getDownX(), pPointer.getDownY()) - pMovementLimitPx >=
                distanceFormula(mUpX, mUpY, pPointer.getUpX(), pPointer.getUpY());
    }
}