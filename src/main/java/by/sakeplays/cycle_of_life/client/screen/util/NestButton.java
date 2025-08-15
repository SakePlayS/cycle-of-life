package by.sakeplays.cycle_of_life.client.screen.util;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public class NestButton extends Button {
    public int type;

    protected NestButton(int x, int y, int width, int height, Component message, OnNestButtonPress onPress, CreateNarration createNarration, int type) {
        super(x, y, width, height, message, b -> {}, createNarration);
        this.type = type;
    }

    public static NestButtonBuilder nestButtonBuilder(Component message, OnNestButtonPress onPress) {
        return new NestButtonBuilder(message, onPress);
    }

    public static class NestButtonBuilder {
        int type;
        private final Component message;
        private final OnNestButtonPress onPress;
        @Nullable
        private Tooltip tooltip;
        private int x;
        private int y;
        private int width = 150;
        private int height = 20;
        private CreateNarration createNarration;

        public NestButtonBuilder(Component message, OnNestButtonPress onPress) {
            this.createNarration = Button.DEFAULT_NARRATION;
            this.message = message;
            this.onPress = onPress;
        }

        public NestButtonBuilder pos(int x, int y) {
            this.x = x;
            this.y = y;
            return this;
        }

        public NestButtonBuilder width(int width) {
            this.width = width;
            return this;
        }

        public NestButtonBuilder size(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        public NestButtonBuilder bounds(int x, int y, int width, int height) {
            return this.pos(x, y).size(width, height);
        }

        public NestButtonBuilder tooltip(@Nullable Tooltip tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        public NestButtonBuilder createNarration(CreateNarration createNarration) {
            this.createNarration = createNarration;
            return this;
        }

        public NestButton build() {
            return new NestButton(x, y, width, height, message, onPress, createNarration, type);
        }


        public NestButtonBuilder type(int type) {
            this.type = type;
            return this;
        }
    }

    @Override
    public void onPress() {
        this.onPress.onPress(this);
    }

    @OnlyIn(Dist.CLIENT)
    public interface OnNestButtonPress {
        void onPress(NestButton var1);
    }
}

