package me.sshcrack.fairylights.client.gui.component;

import com.mojang.blaze3d.systems.RenderSystem;
import me.sshcrack.fairylights.client.gui.EditLetteredConnectionScreen;
import me.sshcrack.fairylights.util.styledstring.StyledString;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public final class ColorButton extends ButtonWidget {
    private static final int TEX_U = 0;

    private static final int TEX_V = 0;

    private Formatting displayColor;

    private float displayColorR;

    private float displayColorG;

    private float displayColorB;

    public ColorButton(final int x, final int y, final Text msg, final ButtonWidget.PressAction onPress) {
        super(x, y, 20, 20, msg, onPress);
    }

    public void setDisplayColor(final Formatting color) {
        this.displayColor = color;
        final int rgb = StyledString.getColor(color);
        this.displayColorR = (rgb >> 16 & 0xFF) / 255F;
        this.displayColorG = (rgb >> 8 & 0xFF) / 255F;
        this.displayColorB = (rgb & 0xFF) / 255F;
    }

    public Formatting getDisplayColor() {
        return this.displayColor;
    }

    public void removeDisplayColor() {
        this.displayColor = null;
    }

    public boolean hasDisplayColor() {
        return this.displayColor != null;
    }

    @Override
    public void renderButton(final MatrixStack stack, final int mouseX, final int mouseY, final float delta) {
        if (this.visible) {
            RenderSystem.setShaderTexture(0, EditLetteredConnectionScreen.WIDGETS_TEXTURE);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            this.drawTexture(stack, this.x, this.y, TEX_U, this.isHovered ? TEX_V + this.height : TEX_V, this.width, this.height);
            if (this.displayColor != null) {
                this.drawTexture(stack, this.x, this.y, TEX_U + this.width, TEX_V, this.width, this.height);
                RenderSystem.setShaderColor(this.displayColorR, this.displayColorG, this.displayColorB, 1.0F);
                this.drawTexture(stack, this.x, this.y, TEX_U + this.width, TEX_V + this.height, this.width, this.height);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
    }
}
