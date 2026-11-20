package advancedshader.core.patcher.optifine;

import org.objectweb.asm.tree.MethodNode;

import advancedshader.core.patcher.Patcher;
import advancedshader.core.patcher.Patcher.Patch;

/**
 * net.optifine.CustomSky, not patched.
 *
 * The upstream patch inserts a CUSTOM_SKY render stage marker before one of the early
 * returns of renderSky, so that a shaderpack can tell a custom sky pass apart from a
 * vanilla one. In OptiFine G5 (1.12.2) renderSky is an instance method whose first
 * local is `this`. In H5 (1.10.2) it is `public static void renderSky(World,
 * TextureManager, float)`: there is no `this`, every local index shifts down by one, and
 * the early-return sequence the patch anchors on is a different one (the method starts
 * with a null test on worldSkyLayers rather than with the layer list the 1.12.2 body
 * walks). Anchoring on "any RETURN, then a label" would match at an arbitrary point and
 * insert the marker in the wrong place, which is worse than not inserting it.
 *
 * What is lost: a pack that branches on the CUSTOM_SKY stage sees it as part of the
 * default stage. That affects sky layering decisions in a few packs, not whether the
 * pipeline runs.
 */
@Patch("net.optifine.CustomSky")
public class CustomSkyPatcher extends Patcher {
}
