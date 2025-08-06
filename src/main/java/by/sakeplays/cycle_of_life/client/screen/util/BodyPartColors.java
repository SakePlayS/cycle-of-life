package by.sakeplays.cycle_of_life.client.screen.util;

public class BodyPartColors {

    private ColorOption maleDisplay, markings, body, flank, belly, eyes;

    public BodyPartColors(ColorOption maleDisplay, ColorOption markings, ColorOption body, ColorOption flank,
                          ColorOption belly, ColorOption eyes) {
        this.maleDisplay = maleDisplay;
        this.markings = markings;
        this.body = body;
        this.flank = flank;
        this.belly = belly;
        this.eyes = eyes;
    }

    public ColorOption getMaleDisplay() {
        return maleDisplay;
    }

    public void setMaleDisplay(ColorOption maleDisplay) {
        this.maleDisplay = maleDisplay;
    }

    public ColorOption getMarkings() {
        return markings;
    }

    public void setMarkings(ColorOption markings) {
        this.markings = markings;
    }

    public ColorOption getBody() {
        return body;
    }

    public void setBody(ColorOption body) {
        this.body = body;
    }

    public ColorOption getFlank() {
        return flank;
    }

    public void setFlank(ColorOption flank) {
        this.flank = flank;
    }

    public ColorOption getBelly() {
        return belly;
    }

    public void setBelly(ColorOption belly) {
        this.belly = belly;
    }

    public ColorOption getEyes() {
        return eyes;
    }

    public void setEyes(ColorOption eyes) {
        this.eyes = eyes;
    }
}
