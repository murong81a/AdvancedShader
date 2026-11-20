package advancedshader.core.patcher.optifine;

import org.objectweb.asm.tree.MethodNode;

import advancedshader.core.patcher.Patcher;
import advancedshader.core.patcher.Patcher.Patch;

@Patch("net.optifine.shaders.config.ShaderPackParser")
public class ShaderPackParserPatcher extends Patcher {

    @MethodPatch("resolveIncludes(Ljava/io/BufferedReader;Ljava/lang/String;Lnet/optifine/shaders/IShaderPack;ILjava/util/List;I)Ljava/io/BufferedReader;")
    public void resolveIncludes(MethodNode method) {
        patch("replace the shader version macro", method,
                ByteCode.InvokeStatic("net/optifine/shaders/config/ShaderMacros", "getFixedMacroLines", "()Ljava/lang/String;"),
                inject(ByteCode.InvokeStatic(FORWARDFEATURES, "replaceShaderVersionMacro", "(Ljava/lang/String;)Ljava/lang/String;")));

        // Convert the assembled shader source to a core profile before OptiFine compiles it.
        //
        // This is the step that makes modern packs compile at all. A 1.16 pack writes GLSL
        // 150+ and uses integer modulo and texelFetch, and OptiFine H5 compiles with a
        // compatibility profile that rejects both:
        //
        //   0(19) : error C7548: '%' requires "#extension GL_EXT_gpu_shader4 : enable"
        //   0(19) : error C7531: global function texelFetch2D requires ...
        //
        // Hook.CoreProfile already rewrites "#version 120" to "#version 150 compatibility" and
        // maps the legacy gl_* attributes onto core-profile names. It was never called: the two
        // upstream injections that would have called it target a G5 API (convertVersion and a
        // three-argument convertCoreProfile) that does not exist in H5, so they had been
        // commented out and every pack failed to link.
        //
        // The insertion point is the single place where resolveIncludes builds the reader it
        // returns, immediately before its one areturn. The value on the stack is already a
        // BufferedReader and the hook takes and returns one, so the stack shape is unchanged
        // and no frame information is involved.
        patch("convert the shader source to the core profile", method,
                ByteCode.InvokeSpecial("java/io/BufferedReader", "<init>", "(Ljava/io/Reader;)V"),
                inject(ByteCode.InvokeStatic(COREPROFILE, "convertCoreProfile",
                        "(Ljava/io/BufferedReader;)Ljava/io/BufferedReader;")));
    }

    @MethodPatch("parseBlendStates(Ljava/util/Properties;)V")
    public void parseBlendStates(MethodNode method) {
        patch("增加blend.<program>.<buffer>配置", method,
                ByteCode.ALoad(3),
                ByteCode.Ldc("."),
                ByteCode.InvokeStatic("Config", "tokenize", "(Ljava/lang/String;Ljava/lang/String;)[Ljava/lang/String;"),
                ByteCode.AStore(4),
                inject(ByteCode.ALoad(4)),
                inject(ByteCode.ALoad(0)),
                inject(ByteCode.ALoad(3)),
                inject(ByteCode.InvokeVirtual("java/util/Properties", "getProperty", "(Ljava/lang/String;)Ljava/lang/String;")),
                inject(ByteCode.InvokeStatic(BLEND, "parseBlendBuffer", "([Ljava/lang/String;Ljava/lang/String;)V")));
    }

    @MethodPatch("collectShaderOptions(Lnet/optifine/shaders/IShaderPack;Ljava/lang/String;[Ljava/lang/String;Ljava/util/Map;)V")
    public void collectShaderOptions(MethodNode method) {
        patch("解析几何着色器与计算着色器中的配置", method,
                ByteCode.New("java/lang/StringBuilder"),
                ByteCode.Dup(),
                ByteCode.InvokeSpecial("java/lang/StringBuilder", "<init>", "()V"),
                ByteCode.ALoad(1),
                ByteCode.InvokeVirtual("java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;"),
                ByteCode.Ldc("/"),
                ByteCode.InvokeVirtual("java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;"),
                ByteCode.ALoad(5),
                ByteCode.InvokeVirtual("java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;"),
                ByteCode.Ldc(".vsh"),
                ByteCode.InvokeVirtual("java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;"),
                ByteCode.InvokeVirtual("java/lang/StringBuilder", "toString", "()Ljava/lang/String;"),
                ByteCode.AStore(6),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.New("java/lang/StringBuilder"),
                ByteCode.Dup(),
                ByteCode.InvokeSpecial("java/lang/StringBuilder", "<init>", "()V"),
                ByteCode.ALoad(1),
                ByteCode.InvokeVirtual("java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;"),
                ByteCode.Ldc("/"),
                ByteCode.InvokeVirtual("java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;"),
                ByteCode.ALoad(5),
                ByteCode.InvokeVirtual("java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;"),
                ByteCode.Ldc(".fsh"),
                ByteCode.InvokeVirtual("java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;"),
                ByteCode.InvokeVirtual("java/lang/StringBuilder", "toString", "()Ljava/lang/String;"),
                ByteCode.AStore(7),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.ALoad(0),
                ByteCode.ALoad(6),
                ByteCode.ALoad(3),
                ByteCode.InvokeStatic("net/optifine/shaders/config/ShaderPackParser", "collectShaderOptions", "(Lnet/optifine/shaders/IShaderPack;Ljava/lang/String;Ljava/util/Map;)V"),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.ALoad(0),
                ByteCode.ALoad(7),
                ByteCode.ALoad(3),
                ByteCode.InvokeStatic("net/optifine/shaders/config/ShaderPackParser", "collectShaderOptions", "(Lnet/optifine/shaders/IShaderPack;Ljava/lang/String;Ljava/util/Map;)V"),
                inject(ByteCode.New("java/lang/StringBuilder")),
                inject(ByteCode.Dup()),
                inject(ByteCode.InvokeSpecial("java/lang/StringBuilder", "<init>", "()V")),
                inject(ByteCode.ALoad(1)),
                inject(ByteCode.InvokeVirtual("java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;")),
                inject(ByteCode.Ldc("/")),
                inject(ByteCode.InvokeVirtual("java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;")),
                inject(ByteCode.ALoad(5)),
                inject(ByteCode.InvokeVirtual("java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;")),
                inject(ByteCode.Ldc(".gsh")),
                inject(ByteCode.InvokeVirtual("java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;")),
                inject(ByteCode.InvokeVirtual("java/lang/StringBuilder", "toString", "()Ljava/lang/String;")),
                inject(ByteCode.AStore(6)),
                inject(ByteCode.New("java/lang/StringBuilder")),
                inject(ByteCode.Dup()),
                inject(ByteCode.InvokeSpecial("java/lang/StringBuilder", "<init>", "()V")),
                inject(ByteCode.ALoad(1)),
                inject(ByteCode.InvokeVirtual("java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;")),
                inject(ByteCode.Ldc("/")),
                inject(ByteCode.InvokeVirtual("java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;")),
                inject(ByteCode.ALoad(5)),
                inject(ByteCode.InvokeVirtual("java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;")),
                inject(ByteCode.Ldc(".csh")),
                inject(ByteCode.InvokeVirtual("java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;")),
                inject(ByteCode.InvokeVirtual("java/lang/StringBuilder", "toString", "()Ljava/lang/String;")),
                inject(ByteCode.AStore(7)),
                inject(ByteCode.ALoad(0)),
                inject(ByteCode.ALoad(6)),
                inject(ByteCode.ALoad(3)),
                inject(ByteCode.InvokeStatic("net/optifine/shaders/config/ShaderPackParser", "collectShaderOptions", "(Lnet/optifine/shaders/IShaderPack;Ljava/lang/String;Ljava/util/Map;)V")),
                inject(ByteCode.ALoad(0)),
                inject(ByteCode.ALoad(7)),
                inject(ByteCode.ALoad(3)),
                inject(ByteCode.InvokeStatic("net/optifine/shaders/config/ShaderPackParser", "collectShaderOptions", "(Lnet/optifine/shaders/IShaderPack;Ljava/lang/String;Ljava/util/Map;)V")));
    }
}
