package by.sakeplays.cycle_of_life.client.screen.util;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class BrightnessSlider extends AbstractSliderButton {

    private final Consumer<Double> onValueChanged;


    public BrightnessSlider(int x, int y, int width, int height, Component message, double value, Consumer<Double> onValueChanged) {
        super(x, y, width, height, message, value);
        this.onValueChanged = onValueChanged;
    }

    @Override
    protected void applyValue() {
        onValueChanged.accept(value);
    }

    @Override
    protected void updateMessage() {
    }

    public void setSliderValue(double newValue) {
        value = newValue;
        applyValue();
    }
}
