package rikka.librikka.model.quadbuilder;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.client.renderer.model.BakedQuad;

public interface IBakeable {
    /**
     * Convert vertex/texture data represented by this class to BakedQuads,
     * which can be then consumed by Minecraft's rendering system
     *
     * @param list BakedQuads will be added to this list, must NOT be null!!!
     */
    void bake(@Nonnull List<BakedQuad> list);
}
