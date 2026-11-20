package advancedshader.core.patcher.forge;

import org.objectweb.asm.tree.MethodNode;

import advancedshader.core.patcher.Patcher;
import advancedshader.core.patcher.Patcher.Patch;

/**
 * Item side UV fix, not ported.
 *
 * The upstream 1.12.2 version rewrites a long instruction sequence inside Forge's
 * ItemLayerModel.buildSideQuad, expressed entirely in 1.12.2 terms: EnumFacing is `fa`,
 * TextureAtlasSprite is `cdq`, and buildQuad takes twenty float arguments. In 1.10.2
 * that method has a different signature and a different body, so the sequence this
 * patch matches does not exist. Rather than leave a patch that can only throw at class
 * load, the class keeps no method patch and therefore does nothing: an item side UV
 * that is slightly wrong is a cosmetic defect, while a failed patch aborts the whole
 * transformer and takes the shader pipeline with it.
 *
 * If this is wanted later, the patch has to be written against the 1.10.2 body of
 * net.minecraftforge.client.model.ItemLayerModel.buildSideQuad, which needs a fresh
 * disassembly of that method rather than a translation of this one.
 */
@Patch("net.minecraftforge.client.model.ItemLayerModel")
public class ItemLayerModelPatcher extends Patcher {
}
