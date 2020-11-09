package rikka.librikka.gui;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import rikka.librikka.Utils;

@OnlyIn(Dist.CLIENT)
public abstract class GuiDirectionSelector extends Widget{
	public static final ITextComponent defaultTitle = new StringTextComponent("GuiDirectionSelector");
    private static final int[][] rotationMatrix = {
            {2, 3, 4, 5},
            {3, 2, 5, 4},
            {4, 5, 3, 2},
            {5, 4, 2, 3}
    };
    private final GuiDirectionSelector.GuiDirectionSelectorButton[] buttons;
    public Direction red, green;
    public boolean showTooltip = true;
    public boolean showGreen = true;
    
    public GuiDirectionSelector(int x, int y) {
    	this(x, y, Utils.getPlayerSightHorizontal(Minecraft.getInstance().player));
    }
    
    public GuiDirectionSelector(int x, int y, Direction playerSight) {
    	super(x, y, 31, 20, defaultTitle);
    	
        int sight;
        if (playerSight == null)
            sight = 4;
        else if (playerSight == Direction.NORTH ||
                playerSight == Direction.SOUTH ||
                playerSight == Direction.WEST ||
                playerSight == Direction.EAST)
            sight = playerSight.ordinal() - 2 & 3;
        else
            sight = 4;

        this.buttons = new GuiDirectionSelector.GuiDirectionSelectorButton[6];

        buttons[0] = new GuiDirectionSelector.GuiDirectionSelectorButton(this, x + 23, y + 6, GuiDirectionSelector.GuiDirectionSelectorButton.TYPE_DOWN, Direction.DOWN);        //D
        buttons[1] = new GuiDirectionSelector.GuiDirectionSelectorButton(this, x + 6, y + 6, GuiDirectionSelector.GuiDirectionSelectorButton.TYPE_UP, Direction.UP);            //U
        buttons[2] = new GuiDirectionSelector.GuiDirectionSelectorButton(this, x + 3, y, GuiDirectionSelector.GuiDirectionSelectorButton.TYPE_HORIZONTAL, Direction.byIndex(rotationMatrix[sight][0]));    //N
        buttons[3] = new GuiDirectionSelector.GuiDirectionSelectorButton(this, x + 3, y + 17, GuiDirectionSelector.GuiDirectionSelectorButton.TYPE_HORIZONTAL, Direction.byIndex(rotationMatrix[sight][1]));    //S
        buttons[4] = new GuiDirectionSelector.GuiDirectionSelectorButton(this, x, y + 3, GuiDirectionSelector.GuiDirectionSelectorButton.TYPE_VERTICAL, Direction.byIndex(rotationMatrix[sight][2]));    //W
        buttons[5] = new GuiDirectionSelector.GuiDirectionSelectorButton(this, x + 17, y + 3, GuiDirectionSelector.GuiDirectionSelectorButton.TYPE_VERTICAL, Direction.byIndex(rotationMatrix[sight][3]));    //E
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float p_render_3_) {
        if (this.visible) {
        	this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        	
        	for (GuiDirectionSelector.GuiDirectionSelectorButton button: buttons) {
                if (button.actualDirection == red)
                	button.state = GuiDirectionSelector.GuiDirectionSelectorButton.STATE_RED;
                else if (button.actualDirection == green)
                	button.state = this.showGreen ? 
                			GuiDirectionSelector.GuiDirectionSelectorButton.STATE_GREEN :
                			GuiDirectionSelector.GuiDirectionSelectorButton.STATE_NO_SELECTION	;
                else
                	button.state = GuiDirectionSelector.GuiDirectionSelectorButton.STATE_NO_SELECTION;
              	
                button.render(matrixStack, mouseX, mouseY, p_render_3_);
        	}
        }
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (this.active && this.visible) {
        	for (GuiDirectionSelector.GuiDirectionSelectorButton button: buttons) {
                if (button.isMouseOver(mouseX, mouseY)) {
                	super.playDownSound(Minecraft.getInstance().getSoundHandler());
                	this.onClick(button.actualDirection, mouseButton);
                	return true;
                }
        	}
           return false;
        } else {
           return false;
        }
     }

    public void set(Direction red, Direction green) {
    	this.red = red;
    	this.green = green;
    }
    
    protected abstract void onClick(Direction selectedDirection, int mouseButton);
    protected abstract ResourceLocation texture();
    protected ITextComponent localizeDirection(Direction direction) {
    	return new StringTextComponent(direction.getString());
    }

    public static final class GuiDirectionSelectorButton extends Widget {
    	public static final ITextComponent defaultText = new StringTextComponent("");
        public static final byte TYPE_HORIZONTAL = 0;
        public static final byte TYPE_VERTICAL = 1;
        public static final byte TYPE_UP = 2;
        public static final byte TYPE_DOWN = 3;
        public static final byte STATE_NO_SELECTION = 0;
        public static final byte STATE_RED = 1;
        public static final byte STATE_GREEN = 2;
        private static final int[] widthList = {14, 3, 8, 8};
        private static final int[] heightList = {3, 14, 8, 8};
        private static final int[][] uList = new int[3][];
        private static final int[][] vList = new int[3][];

        static {
            GuiDirectionSelectorButton.uList[GuiDirectionSelectorButton.STATE_NO_SELECTION] = new int[]{0, 0, 9, 9};
            GuiDirectionSelectorButton.vList[GuiDirectionSelectorButton.STATE_NO_SELECTION] = new int[]{0, 3, 3, 11};
            GuiDirectionSelectorButton.uList[GuiDirectionSelectorButton.STATE_RED] = new int[]{14, 3, 17, 17};
            GuiDirectionSelectorButton.vList[GuiDirectionSelectorButton.STATE_RED] = new int[]{0, 3, 3, 11};
            GuiDirectionSelectorButton.uList[GuiDirectionSelectorButton.STATE_GREEN] = new int[]{28, 6, 25, 25};
            GuiDirectionSelectorButton.vList[GuiDirectionSelectorButton.STATE_GREEN] = new int[]{0, 3, 3, 11};
        }

        protected final GuiDirectionSelector parent;
        protected final Direction actualDirection;
        protected final byte type;
        protected byte state;

        protected GuiDirectionSelectorButton(GuiDirectionSelector parent, int xPos, int yPos, byte type, Direction actualDirection) {
        	super(xPos, yPos,
        			GuiDirectionSelector.GuiDirectionSelectorButton.widthList[type],
        			GuiDirectionSelector.GuiDirectionSelectorButton.heightList[type],
        			parent.localizeDirection(actualDirection));

        	this.parent = parent;
            this.actualDirection = actualDirection;
            this.type = type;
            this.state = 0;
        }

        @Override
		public void render(MatrixStack matrixStack, int mouseX, int mouseY, float p_render_3_) {
            if (!visible || !active)
                return;

            if (parent.showTooltip && this.isMouseOver(mouseX, mouseY))
            	Minecraft.getInstance().currentScreen.renderTooltip(matrixStack, this.getMessage(), mouseX, mouseY);
            
            Minecraft.getInstance().getTextureManager().bindTexture(parent.texture());

            int u = GuiDirectionSelector.GuiDirectionSelectorButton.uList[state][type];
            int v = GuiDirectionSelector.GuiDirectionSelectorButton.vList[state][type];
            blit(matrixStack, x, y, u, v, width, height);
        }        
    }
}
