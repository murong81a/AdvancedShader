package advancedshader.core.patcher.vanilla;

import org.objectweb.asm.tree.MethodNode;

import advancedshader.core.patcher.Patcher;
import advancedshader.core.patcher.Patcher.Patch;

/**
 * ChunkRenderDispatcher is bqt in 1.10.2.
 *
 * Not patched. The upstream patch wraps the VBO upload with a pre/post pair that sets the
 * sprite bounds used by anisotropic filtering. It anchors on ListedRenderChunk.getDisplayList
 * (BlockRenderLayer, CompiledChunk) followed by uploadDisplayList(VertexBuffer, int,
 * RenderChunk), which is the 1.12.2 upload shape.
 *
 * The 1.10.2 body of uploadChunk is different: it branches on an OptiFine capability test and
 * then either calls uploadDisplayList(VertexBuffer, int, RenderChunk) directly or resolves the
 * buffer with getVertexBufferByLayer(int) and calls uploadVertexBuffer. There is no
 * ListedRenderChunk in the path at all, so the anchor does not exist and the patch cannot be
 * translated without guessing at which of the two branches to wrap.
 *
 * What is lost: shader sprite bounds are not refreshed on VBO upload, so the normal and
 * specular maps used for anisotropic filtering keep whatever bounds they were given at atlas
 * load. That affects one texture-data feature, not the program pipeline. Note that the same
 * data is already absent because TextureMapPatcher is also not ported, so this is consistent
 * rather than a new gap.
 */
@Patch("bqt")
public class ChunkRenderDispatcherPatcher extends Patcher {
}
