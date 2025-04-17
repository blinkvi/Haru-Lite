package cc.unknown.util.render.shader;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glVertex2f;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniform2f;
import static org.lwjgl.opengl.GL20.glUniform2i;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUniform4f;
import static org.lwjgl.opengl.GL20.glUseProgram;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import cc.unknown.util.Accessor;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;

public class ShaderUtil implements Accessor {
    private final int programID;
    public static Framebuffer frameBuffer = new Framebuffer(1, 1, false);
    
    public ShaderUtil(String fragmentShaderLoc, String vertexShaderLoc) {
        int program = glCreateProgram();
        int fragmentShaderID = -1;
        int vertexShaderID = -1;

        try {
            String fragPath = getShaderPath(fragmentShaderLoc);

            try (InputStream fragStream = mc.getResourceManager().getResource(new ResourceLocation(fragPath)).getInputStream()) {
                fragmentShaderID = createShader(fragStream, GL_FRAGMENT_SHADER);
            }

            try (InputStream vertStream = mc.getResourceManager().getResource(new ResourceLocation(vertexShaderLoc)).getInputStream()) {
                vertexShaderID = createShader(vertStream, GL_VERTEX_SHADER);
            }

            if (fragmentShaderID != -1) glAttachShader(program, fragmentShaderID);
            if (vertexShaderID != -1) glAttachShader(program, vertexShaderID);

        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException("No se pudieron cargar los shaders: " + fragmentShaderLoc + " / " + vertexShaderLoc);
        }

        glLinkProgram(program);
        int status = glGetProgrami(program, GL_LINK_STATUS);

        if (status == 0) { }

        this.programID = program;
    }

    private String getShaderPath(String key) {
        switch (key) {
            case "roundRectOutline": return "haru/shader/roundRectOutline.frag";
            case "roundedRect": return "haru/shader/roundedRect.frag";
        };
        
        return key;
    }

    public ShaderUtil(String fragmentShaderLoc) {
        this(fragmentShaderLoc, "haru/shader/vertex.vsh");
    }


    public void init() {
        glUseProgram(programID);
    }

    public void unload() {
        glUseProgram(0);
    }

    public int getUniform(String name) {
        return glGetUniformLocation(programID, name);
    }

    public void setUniformf(String name, float... args) {
        int loc = glGetUniformLocation(programID, name);
        switch (args.length) {
            case 1: glUniform1f(loc, args[0]); break;
            case 2: glUniform2f(loc, args[0], args[1]); break;
            case 3: glUniform3f(loc, args[0], args[1], args[2]); break;
            case 4: glUniform4f(loc, args[0], args[1], args[2], args[3]); break;
            default: throw new IllegalArgumentException("Invalid number of arguments for uniform: " + args.length);
        }
    }

    public void setUniformi(String name, int... args) {
        int loc = glGetUniformLocation(programID, name);
        if (args.length > 1) glUniform2i(loc, args[0], args[1]);
        else glUniform1i(loc, args[0]);
    }

    public static void drawQuad(float x, float y, float width, float height) {
        glBegin(GL_QUADS);
        glTexCoord2f(0, 0);
        glVertex2f(x, y);
        glTexCoord2f(0, 1);
        glVertex2f(x, y + height);
        glTexCoord2f(1, 1);
        glVertex2f(x + width, y + height);
        glTexCoord2f(1, 0);
        glVertex2f(x + width, y);
        glEnd();
    }

    public static void drawQuad() {
        ScaledResolution sr = new ScaledResolution(mc);
        float width = (float) sr.getScaledWidth_double();
        float height = (float) sr.getScaledHeight_double();
        glBegin(GL_QUADS);
        glTexCoord2f(0, 1);
        glVertex2f(0, 0);
        glTexCoord2f(0, 0);
        glVertex2f(0, height);
        glTexCoord2f(1, 0);
        glVertex2f(width, height);
        glTexCoord2f(1, 1);
        glVertex2f(width, 0);
        glEnd();
    }

    public static void drawQuad(float width, float height) {
        drawQuad(0.0f, 0.0f, width, height);
    }

    public static void drawFixedQuad() {
        ScaledResolution sr = new ScaledResolution(mc);
        drawQuad((float) (mc.displayWidth / sr.getScaleFactor()), (float) mc.displayHeight / sr.getScaleFactor());
    }

    private int createShader(InputStream inputStream, int shaderType) {
        int shader = glCreateShader(shaderType);
        glShaderSource(shader, readInputStream(inputStream));
        glCompileShader(shader);


        if (glGetShaderi(shader, GL_COMPILE_STATUS) == 0) {
            System.out.println(glGetShaderInfoLog(shader, 4096));
            throw new IllegalStateException(String.format("Shader (%s) failed to compile!", shaderType));
        }

        return shader;
    }

    private String readInputStream(InputStream inputStream) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
            return sb.toString();
        } catch (IOException e) {
            throw new RuntimeException("Error reading shader input stream", e);
        }
    }

}