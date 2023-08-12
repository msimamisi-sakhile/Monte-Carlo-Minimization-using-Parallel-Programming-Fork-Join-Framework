package MonteCarloMini;

import java.util.concurrent.RecursiveAction;
import java.util.concurrent.ForkJoinPool;
import java.util.Random;

class MonteCarloMinimizationParallel{
    static final boolean DEBUG=false;

    static long startTime = 0;
    static long endTime = 0;

    //timers - note milliseconds
    private static void tick(){
        startTime = System.currentTimeMillis();
    }
    private static void tock(){
        endTime=System.currentTimeMillis();
    }

    public static void main (String[] args){

        int rows, columns; //grid size
        double xmin, xmax, ymin, ymax; //x and y terrain limits
        TerrainArea terrain;  //object to store the heights and grid points visited by searches
        double searches_density;	// Density - number of Monte Carlo  searches per grid position - usually less than 1!

        int num_searches;		// Number of searches
        Search [] searches;		// Array of searches
        Random rand = new Random();  //the random number generator

        if (args.length!=7) {
            System.out.println("Incorrect number of command line arguments provided.");
            System.exit(0);
        }
        /* Read argument values */
        rows =Integer.parseInt( args[0] );
        columns = Integer.parseInt( args[1] );
        xmin = Double.parseDouble(args[2] );
        xmax = Double.parseDouble(args[3] );
        ymin = Double.parseDouble(args[4] );
        ymax = Double.parseDouble(args[5] );
        searches_density = Double.parseDouble(args[6] );

        if(DEBUG) {
            /* Print arguments */
            System.out.printf("Arguments, Rows: %d, Columns: %d\n", rows, columns);
            System.out.printf("Arguments, x_range: ( %f, %f ), y_range( %f, %f )\n", xmin, xmax, ymin, ymax );
            System.out.printf("Arguments, searches_density: %f\n", searches_density );
            System.out.printf("\n");
        }

        // Initialize
        terrain = new TerrainArea(rows, columns, xmin,xmax,ymin,ymax);
        num_searches = (int)( rows * columns * searches_density );
        searches= new Search [num_searches];
        for (int i=0;i<num_searches;i++)
            searches[i]=new Search(i+1, rand.nextInt(rows),rand.nextInt(columns),terrain);

        if(DEBUG) {
            /* Print initial values */
            System.out.printf("Number searches: %d\n", num_searches);
            //terrain.print_heights();
        }

    }

}



class Multithreader extends RecursiveAction {

    public Multithreader(){

    }

    @Override
    protected void compute() {


    }
}