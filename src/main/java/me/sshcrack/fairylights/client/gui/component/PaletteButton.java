package me.sshcrack.fairylights.client.gui.component;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.systems.RenderSystem;
import me.sshcrack.fairylights.client.gui.EditLetteredConnectionScreen;
import me.sshcrack.fairylights.util.FLMth;
import me.sshcrack.fairylights.util.styledstring.StyledString;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.ArrayUtils;

public class PaletteButton extends ButtonWidget {
    private static final int TEX_U = 0;

    private static final int TEX_V = 40;

    private static final int SELECT_U = 28;

    private static final int SELECT_V = 40;

    private static final int COLOR_U = 34;

    private static final int COLOR_V = 40;

    private static final int COLOR_WIDTH = 6;

    private static final int COLOR_HEIGHT = 6;

    private static final Formatting[] IDX_COLOR = {Formatting.WHITE, Formatting.GRAY, Formatting.DARK_GRAY, Formatting.BLACK, Formatting.RED, Formatting.DARK_RED, Formatting.YELLOW, Formatting.GOLD, Formatting.LIGHT_PURPLE, Formatting.DARK_PURPLE, Formatting.GREEN, Formatting.DARK_GREEN, Formatting.BLUE, Formatting.DARK_BLUE, Formatting.AQUA, Formatting.DARK_AQUA};

    private static final int[] COLOR_IDX = FLMth.invertMap(IDX_COLOR, Formatting::ordinal);

    private final ColorButton colorBtn;

    public PaletteButton(final int x, final int y, final ColorButton colorBtn, final Text msg, final ButtonWidget.PressAction pressable) {
        super(x, y, 28, 28, msg, pressable);
        this.colorBtn = colorBtn;
    }

    @Override
    public void onPress() {
        this.colorBtn.setDisplayColor(IDX_COLOR[(ArrayUtils.indexOf(IDX_COLOR, this.colorBtn.getDisplayColor()) + 1) % IDX_COLOR.length]);
        super.onPress();
    }

    @Override
    public void onClick(final double mouseX, final double mouseY) {
        final int idx = this.getMouseOverIndex(mouseX, mouseY);
        if (idx > -1) {
            this.colorBtn.setDisplayColor(IDX_COLOR[idx]);
            super.onPress();
        }
    }

    @Override
    public void renderButton(final MatrixStack stack, final int mouseX, final int mouseY, final float delta) {
        if (this.visible) {
            RenderSystem.setShaderTexture(0, EditLetteredConnectionScreen.WIDGETS_TEXTURE);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            this.drawTexture(stack, this.x, this.y, TEX_U, TEX_V, this.width, this.height);
            if (this.colorBtn.hasDisplayColor()) {
                final int idx = COLOR_IDX[this.colorBtn.getDisplayColor().ordinal()];
                final int selectX = this.x + 2 + (idx % 4) * 6;
                final int selectY = this.y + 2 + (idx / 4) * 6;
                this.drawTexture(stack, selectX, selectY, SELECT_U, SELECT_V, COLOR_WIDTH, COLOR_HEIGHT);
            }
            for (int i = 0; i < IDX_COLOR.length; i++) {
                final Formatting color = IDX_COLOR[i];
                final int rgb = StyledString.getColor(color);
                final float r = (rgb >> 16 & 0xFF) / 255F;
                final float g = (rgb >> 8 & 0xFF) / 255F;
                final float b = (rgb & 0xFF) / 255F;
                RenderSystem.setShaderColor(r, g, b, 1.0F);
                this.drawTexture(stack, this.x + 2 + (i % 4) * 6, this.y + 2 + i / 4 * 6, COLOR_U, COLOR_V, COLOR_WIDTH, COLOR_HEIGHT);
            }
            final int selectIndex = this.getMouseOverIndex(mouseX, mouseY);
            if (selectIndex > -1) {
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(GlConst.GL_SRC_ALPHA, GlConst.GL_ONE_MINUS_SRC_ALPHA);
                RenderSystem.setShaderColor(1, 1, 1, 0.5F);
                final int hoverSelectX = this.x + 2 + selectIndex % 4 * 6;
                final int hoverSelectY = this.y + 2 + selectIndex / 4 * 6;
                this.drawTexture(stack, hoverSelectX, hoverSelectY, SELECT_U, SELECT_V, COLOR_WIDTH, COLOR_HEIGHT);
                RenderSystem.disableBlend();
            }
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    private int getMouseOverIndex(final double mouseX, final double mouseY) {
        final int relX = MathHelper.floor(mouseX - this.x - 3);
        final int relY = MathHelper.floor(mouseY - this.y - 3);
        if (relX < 0 || relY < 0 || relX > 22 || relY > 22) {
            return -1;
        }
        final int bucketX = relX % 6;
        final int bucketY = relY % 6;
        if (bucketX > 3 || bucketY > 3) {
            return -1;
        }
        final int x = relX / 6;
        final int y = relY / 6;
        return x + y * 4;
    }
}
