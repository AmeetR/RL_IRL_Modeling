package org.hswgt.teachingbox.core.rl.plot;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.geom.Point2D;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.ericaro.surfaceplotter.JSurfacePanel;
import net.ericaro.surfaceplotter.Mapper;
import net.ericaro.surfaceplotter.ProgressiveSurfaceModel;

import org.apache.log4j.Logger;


/**
 * This class plots a given 3D-function via the surfaceplot library. The 
 * function should be given as a @Mapper object.   
 * 
 * The function to plot must be set via setMapper(...). Otherwise a demo function is plotted. 
 * @author Michel Tokic
 *
 */
public class FunctionPlotter3D implements Plotter, java.io.Serializable {
   
    /**
	 * 
	 */
	private static final long serialVersionUID = 6342783434822242875L;

	/**
     * Plotting style
     */
    public static enum Style {
        /** generates a contour plot */
        CONTOUR,
        /** generates a surface plot */
        SURFACE,
        /** generates a density plot */
        DENSITY,
    }
    

    JFrame jf = new JFrame(); 
    JPanel panel = new JPanel(); 
    JSurfacePanel surfacePanel = new JSurfacePanel();
    ProgressiveSurfaceModel model;
    JToolBar toolBar = new JToolBar();
    JSlider slider = new JSlider();
    Mapper mapper=DEMO_FUNCTION_MAPPER;
    
    // work with value cache, which prevents running multiple time through the neural network
    HashMap <Point2D.Float, Float> valueCache = new HashMap<Point2D.Float, Float>();
    HashMap <Point2D.Float, Float> oldValueCache = new HashMap<Point2D.Float, Float>();
    
	// Logger
    private final static Logger log4j = Logger.getLogger("FunctionPlotter3D");
    private String title = "plotting title";
    private Style style = Style.SURFACE;
    //private float zMin = Float.POSITIVE_INFINITY; 
    //private float zMax = Float.NEGATIVE_INFINITY;

    
    // create demofunction mapper
    public static final Mapper DEMO_FUNCTION_MAPPER = new Mapper() {
    	public float f1( float x, float y)
        {
			float r = x*x+y*y;
            if (r == 0 ) return 1f;
            return (float)( Math.sin(r)/(r));
        }
       
        public float f2( float x, float y)
        {
            return (float)(Math.sin(x*y));
        }
    };

    
	// initialize value cache mapper. Values are cached in a HashMap
	// for preventing running through a neural networks several times, which cases a lot of overhead.   
	private Mapper cacheMapper = new Mapper() {
	
		public synchronized float f1( float x, float y) {
			//return mapper.f1(x, y);
			
			Point2D.Float dataPoint = new Point2D.Float(x,y);
    		float value = 0;
    		
    		if (valueCache.get(dataPoint) != null) {
    			log4j.trace("from cache: " + dataPoint);
    			return valueCache.get(dataPoint);
    		} else {
    			log4j.trace("null => f1(x,y)=" + dataPoint + "=> ");
    			if (mapper == null) {
    				log4j.error("NO MAPPER DEFINED!");
    				System.exit(-1);
    			} else {
    				value = mapper.f1(x,  y);
    				log4j.trace(" mapper.f1 " + value);
    			}
    			
    			//valueCache.put(dataPoint, value);
    			valueCache.put(new Point2D.Float(x,y), new Float (value));
    			return value;
    		}
        }
    	// required by conturplot library, but not used in our case
        public synchronized float f2( float x, float y) {
        	return f1(x,y);
        }
    };

    
	/** 
	 constructor with title and style
	 * @param title The title
	 * @param style The Style
	 */
	public FunctionPlotter3D(String title, Style style) {
		this.style = style;
		this.title = title;
		this.initGUI();
	}
	
    /**
     * default constructor
     */
    public FunctionPlotter3D() {
		this.initGUI();
    }

    private void initGUI() {
    	
    	jf.setTitle(title);
    	
        panel  = new JPanel();
        surfacePanel = new JSurfacePanel();

    	panel.setLayout(new BorderLayout());
        surfacePanel.setTitleText(title);
        surfacePanel.setBackground(Color.white);
        surfacePanel.setTitleFont(surfacePanel.getTitleFont().deriveFont(surfacePanel.getTitleFont().getStyle() | Font.BOLD, surfacePanel.getTitleFont().getSize() + 6f));
        surfacePanel.setConfigurationVisible(false);
        
        panel.add(surfacePanel, BorderLayout.CENTER);
        jf.getContentPane().add(panel);
        
        // add toolbar
        slider.setMaximum(6);
        slider.setValue(0);
        slider.setPaintTicks(true);
        slider.setSnapToTicks(true);
        slider.setMinorTickSpacing(1);
        slider.setMajorTickSpacing(1);
        slider.setPaintLabels(true);
        slider.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                	//System.out.println ("state changed");
                	if (!slider.getValueIsAdjusting())  {
                		model.setCurrentDefinition(slider.getValue());
                	}
                }
        });
        toolBar.add(slider);
        jf.add(toolBar, BorderLayout.NORTH);

        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setPreferredSize(new Dimension(800, 600));
        
        // update the data model
        jf.setResizable(true);
        jf.setVisible(true);
        //model = new ProgressiveSurfaceModel();    		

        model = new ProgressiveSurfaceModel();  		
    }

    /**
     * Sets the function object, which we want to plot.  
     * Initializes the internal plotting components
     * @param functionMapper the @Mapper object
     */
    public void setMapper(Mapper functionMapper) {
    	
    	this.mapper = functionMapper;
    	        
		// set cache mapper to model
    	model.setMapper(cacheMapper);

        // set to model to surface panel
		surfacePanel.setModel(model);
		
        jf.setVisible(true);
		jf.pack();
    }

	public void plot() {
		
		// setup empty value cache
		//oldValueCache = valueCache; // memorize object until the next episode    
		//valueCache = new HashMap<Point2D.Float, Float>();

		valueCache.clear();
			
        model.setDisplayXY(true);
        model.setDisplayZ(true);        
		model.setDataAvailable(true);
		//model.setAutoScaleZ(true);
		//model.setBothFunction(false);
		//model.setFirstFunctionOnly(true);
		
		model.setDataAvailable(true);
        model.plot().execute();

        this.setStyle(style);
		//model.setAutoScaleZ(true);
        //model.autoScale();

        jf.pack();
	}
	
    /**
	 * set's the title
	 * @param title The title
	 */
	public void setTitle(String title) {
		this.title = title;
        surfacePanel.setTitleText(title);
	}
	
	/**
	 * sets the plotting style (e.g. Style.{CONTOUR|SURFACE|DENSITY}
	 * @param style The Style
	 */
	public void setStyle(Style style) {
		this.style = style;
		if (this.style.equals(Style.CONTOUR)) {
			model.setSurfaceType(false);			
			model.setDensityType(false);			
			model.setContourType(true);
		} else if (this.style.equals(Style.SURFACE)) {
			model.setDensityType(false);			
			model.setContourType(false);
			model.setSurfaceType(true);			
		} else if (this.style.equals(Style.DENSITY)) {
			model.setSurfaceType(false);			
			model.setContourType(false);
			model.setDensityType(true);			
		}
	}
		
	/**
	 * setup the axis-labels
	 * @param xlabel The x-axis label
	 * @param ylabel The y-axis label
	 * @param zlabel (currently not supported by surfaceplotter library)
	 */
	public void setLabels (String xlabel, String ylabel, String zlabel) {		
		surfacePanel.getSurface().setXLabel(xlabel);
		surfacePanel.getSurface().setYLabel(ylabel);
	} 
	
	/**
	 * setup the data ranges
	 * @param xbounds The x-axis boundaries
	 * @param ybounds The y-axis boundaries
	*/
	public void setBounds(double[] xbounds, double[] ybounds) {
		if (xbounds != null) {
			model.setXMin((float)xbounds[0]);
			model.setXMax((float)xbounds[1]);
		} if (ybounds != null) {
			model.setYMin((float)ybounds[0]);
			model.setYMax((float)ybounds[1]);
		}
	}
	

	/**
	 * sets the filename for export of the graph
	 * @param filename The filename
	 */
	public void setFilename (String filename) {
	}
	
    /**
     * A simple plotting example
     * @param args The command-line arguments
     * @throws Exception An Exception
     */
    public static void main(String[] args) throws Exception
    {

        FunctionPlotter3D fp = new FunctionPlotter3D("Testplot", Style.CONTOUR);
        //fp.setMapper(FunctionPlotter3D.DEMO_FUNCTION_MAPPER);
        fp.setLabels("x-axis", "y-axis", "Q");
        fp.plot();        
    }
}
