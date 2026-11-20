package advancedshader.core.patcher.vanilla;

import org.objectweb.asm.tree.MethodNode;

import advancedshader.core.patcher.Patcher;
import advancedshader.core.patcher.Patcher.Patch;

/**
 * TextureMap is bwd in 1.10.2.
 *
 * Not patched. The two upstream patches load and unload a normal and a specular map next
 * to each atlas sprite so that anisotropic filtering has height data to work with. They
 * anchor on a sprite load loop that catches an exception and calls printStackTrace, and on
 * a deleteSpriteTexture call in the ceiling-of-the-loop cleanup. Disassembling
 * bwd.b(IResourceManager)V in 1.10.2 finds neither: the sprite loading loop here is a
 * different, shorter shape with no printStackTrace block, and sprite texture deletion is
 * not performed in this method at all.
 *
 * What is lost: the shader sprite maps are not created, so packs that sample a normal or
 * specular texture through the OptiFine sprite extension see no data for it. That is a
 * texture-data feature, not the program pipeline; the pipeline itself does not depend on
 * it.
 *
 * Note that the target method is b(IResourceManager)V (loadTextureAtlas) and not
 * a(IResourceManager)V (loadTexture), which is a re-entrant single-texture path.
 */
@Patch("bwd")
public class TextureMapPatcher extends Patcher {
}
