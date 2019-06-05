//Zachary Reese
//eID: 900893107

package Project7;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.image.BufferedImage;

import javax.media.j3d.Alpha;
import javax.media.j3d.AmbientLight;
import javax.media.j3d.Appearance;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.IndexedQuadArray;
import javax.media.j3d.Material;
import javax.media.j3d.PointLight;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.QuadArray;
import javax.media.j3d.RotationInterpolator;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Texture;
import javax.media.j3d.Texture2D;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JFrame;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.TexCoord2f;

import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.NormalGenerator;
import com.sun.j3d.utils.universe.SimpleUniverse;

public class Mobius extends Applet {

	public static void main(String[] args) {
		new MainFrame(new Mobius(), 800, 600);
	}

	//Create simple universe
	public void init() {
		GraphicsConfiguration gc = SimpleUniverse.getPreferredConfiguration();
		Canvas3D cv = new Canvas3D(gc);
		this.setLayout(new BorderLayout());
		this.add(cv, BorderLayout.CENTER);
		BranchGroup bg = createSceneGraph();
		bg.compile();
		SimpleUniverse su = new SimpleUniverse(cv);
		su.getViewingPlatform().setNominalViewingTransform();
		su.addBranchGraph(bg);
	}

	private BranchGroup createSceneGraph() {
		BranchGroup root = new BranchGroup();
		
		TransformGroup spin = new TransformGroup();
		spin.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		root.addChild(spin);
		
		Appearance ap = createTextureAppearance();
		ap.setMaterial(new Material());
		PolygonAttributes pa = new PolygonAttributes();
		pa.setBackFaceNormalFlip(true);
		pa.setCullFace(PolygonAttributes.CULL_NONE);
		ap.setPolygonAttributes(new PolygonAttributes(PolygonAttributes.POLYGON_FILL, PolygonAttributes.CULL_BACK, 0));
		Shape3D shape = new Shape3D();
		shape.setGeometry(mobius().getGeometryArray());
		
		Transform3D tr = new Transform3D();
        tr.setScale(0.5);
		TransformGroup tg = new TransformGroup(tr);
        tg.addChild(shape);
        spin.addChild(tg);
        shape.setAppearance(ap);         
        Alpha alpha = new Alpha(-1, 10000);
        RotationInterpolator rotate = new RotationInterpolator(alpha, spin);
        BoundingSphere bounds = new BoundingSphere();
        rotate.setSchedulingBounds(bounds);
        spin.addChild(rotate);
        Background background = new Background(1.0f, 1.0f, 1.0f);
        background.setApplicationBounds(bounds);
        root.addChild(background);

        PointLight ptlight = new PointLight(new Color3f(Color.white),
        new Point3f(0.5f,0.3f,1f),
        new Point3f(1f,0.2f,0f));
        ptlight.setInfluencingBounds(bounds);
        root.addChild(ptlight);


		return root;
	}

	private GeometryInfo mobius() {
		
		int rows = 500;
		int cols = 500;
		int p = 4 * ((rows - 1) * (cols - 1));

		IndexedQuadArray iqa = new IndexedQuadArray(rows * cols,
				GeometryArray.COORDINATES|QuadArray.NORMALS|QuadArray.TEXTURE_COORDINATE_2, p);
		Point3d[] vertices = new Point3d[rows * cols];
		int index = 0;
		
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				double u = i * (4 * (Math.PI)) / (rows - 1);
				double v = -0.3 + (j * (0.6 / (cols - 1)));
				double x = (1 + v * Math.cos(u / 2)) * Math.cos(u);
				double y = (1 + v * Math.cos(u / 2)) * Math.sin(u);
				double z = v * Math.sin(u / 2);
				vertices[index] = new Point3d(x, y, z);
				index++;
			}
		}

		iqa.setCoordinates(0, vertices);
		index = 0;

		for (int i = 0; i < rows - 1; i++) {
			for (int j = 0; j < cols - 1; j++) {
				TexCoord2f tex0 = new TexCoord2f(j*1f/rows, 1f);
			    TexCoord2f tex1 = new TexCoord2f(j*1f/rows, 0f);
				iqa.setCoordinateIndex(index, i * rows + j);
				iqa.setTextureCoordinate(0, i * rows + j, tex0);
				index++;
				iqa.setCoordinateIndex(index, i * rows + j + 1);
				iqa.setTextureCoordinate(0, i * rows + j + 1, tex1);
				index++;
				
				tex0 = new TexCoord2f((j+1)*1f/rows, 1f);
			    tex1 = new TexCoord2f((j+1)*1f/rows, 0f);
				iqa.setCoordinateIndex(index, (i + 1) * rows + j + 1);
				iqa.setTextureCoordinate(0, (i + 1) * rows + j + 1, tex1);
				index++;
				iqa.setCoordinateIndex(index, (i + 1) * rows + j);
				iqa.setTextureCoordinate(0, (i + 1) * rows + j, tex0);
				index++;
			}
		}

		GeometryInfo gi = new GeometryInfo(iqa);
		NormalGenerator ng = new NormalGenerator();
		ng.generateNormals(gi);
		return gi;
	}
	
	Appearance createTextureAppearance(){
	    Appearance ap = new Appearance();
	    BufferedImage bi = new BufferedImage(512,128, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g2 = (Graphics2D)bi.getGraphics();
	    g2.setColor(Color.white);
	    g2.fillRect(0, 0, 512,128);
	    g2.setFont(new Font("Serif", Font.BOLD, 48));
	    g2.setColor(new Color(200,0,0));
	    g2.drawString("Mobius Strip",0,100);
	    ImageComponent2D image = 
	      new ImageComponent2D(ImageComponent2D.FORMAT_RGBA, bi);
	    Texture2D texture = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA,
	    image.getWidth(), image.getHeight());
	    texture.setImage(0, image);
	    texture.setMagFilter(Texture.BASE_LEVEL_LINEAR);
	    ap.setTexture(texture);
	    //combine texture and lighting
	    TextureAttributes texatt = new TextureAttributes();
	    texatt.setTextureMode(TextureAttributes.COMBINE);
	    ap.setTextureAttributes(texatt);
	    ap.setMaterial(new Material());
	    return ap;
	  }
}
