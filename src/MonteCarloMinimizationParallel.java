package MonteCarloMini;

import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

class MonteCarloMinimizationParallel {

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

    public static void main(String[] args) {
        int rows, columns; //grid size
        double xmin, xmax, ymin, ymax; //x and y terrain limits
        TerrainArea terrain;  //object to store the heights and grid points visited by searches
        double searches_density;	// Density - number of Monte Carlo  searches per grid position - usually less than 1!
        int min;
        int num_searches;		// Number of searches
        Search [] searches;		// Array of searches
        Random rand = new Random();  //the random number generator
        /*
        if (args.length!=7) {
            System.out.println("Incorrect number of command line arguments provided.");
            System.exit(0);
        }*/
        /* Read argument values */

        // Accepts user arguments
        Scanner scannerInput = new Scanner(System.in);
        System.out.println("Please enter your arguments [Seperate arguments with a space] ");
        String [] argz = scannerInput.nextLine().split(" ");
        
        rows =Integer.parseInt( argz[0] );
        columns = Integer.parseInt( argz[1] );
        xmin = Double.parseDouble( argz[2] );
        xmax = Double.parseDouble( argz[3] );
        ymin = Double.parseDouble( argz[4] );
        ymax = Double.parseDouble( argz[5] );
        searches_density = Double.parseDouble( argz[6]);

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

        //all searches
        int local_min=Integer.MAX_VALUE;
        int finder =-1;


        //start timer
        tick();
        //---------------------------------START PARALLEL EXECUTION------------------------------------------------------------------//

        try{
            ForkJoinPool searchThreadPool = new ForkJoinPool();
            SearchMultithreader parallelSearchTask = new SearchMultithreader(searches, 0,num_searches,false);
            finder = parallelSearchTask.finder;
            min = searchThreadPool.invoke(parallelSearchTask);
        }
        catch (Exception e){
            System.out.println("Check Multithreader");
            min = 0;
        }
        //---------------------------------STOP PARALLEL EXECUTION------------------------------------------------------------------//
        //end timer
        tock();

        if(DEBUG) {
            /* print final state */
            terrain.print_heights();
            terrain.print_visited();
        }

        System.out.printf("Run parameters\n");
        System.out.printf("\t Rows: %d, Columns: %d\n", rows, columns);
        System.out.printf("\t x: [%f, %f], y: [%f, %f]\n", xmin, xmax, ymin, ymax );
        System.out.printf("\t Search density: %f (%d searches)\n", searches_density,num_searches );

        /*  Total computation time */
        System.out.printf("Time: %d ms\n",endTime - startTime );
        int tmp=terrain.getGrid_points_visited();
        System.out.printf("Grid points visited: %d  (%2.0f%s)\n",tmp,(tmp/(rows*columns*1.0))*100.0, "%");
        tmp=terrain.getGrid_points_evaluated();
        System.out.printf("Grid points evaluated: %d  (%2.0f%s)\n",tmp,(tmp/(rows*columns*1.0))*100.0, "%");

        /* Results*/
        System.out.printf("Global minimum: %d at x=%.1f y=%.1f\n\n", min, terrain.getXcoord(searches[finder].getPos_row()), terrain.getYcoord(searches[finder].getPos_col()) );

    }
}

class SearchMultithreader extends RecursiveTask<Integer> {

    Search [] searches;
    int startPoint, endPoint, finder;
    boolean DEBUG;
    int min;
    private static final int SERIAL_CUTOFF = 551000;

    public SearchMultithreader(Search [] searches, int startPoint, int endPoint, boolean DEBUG) {
        this.searches = searches;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.DEBUG = DEBUG;
        this.min = Integer.MAX_VALUE;
        this.finder = startPoint;
    }

    @Override
    protected Integer compute() {
        int searchWidth = endPoint - startPoint;
        if (searchWidth < SERIAL_CUTOFF){
            for  (int i=startPoint;i<endPoint;i++) {
                int local_min=searches[i].find_valleys();
                if((!searches[i].isStopped())&&(local_min<min)) { //don't look at  those who stopped because hit existing path
                    min=local_min;
                    //finder=i; //keep track of who found it
                }
                if(DEBUG) System.out.println("Search "+searches[i].getID()+" finished at  "+local_min + " in " +searches[i].getSteps());
            }

            return min;
        }
        else{
            int midPoint = (startPoint+endPoint)/2;
            SearchMultithreader leftFork = new SearchMultithreader(searches, startPoint, midPoint, false);
            SearchMultithreader rightFork = new SearchMultithreader(searches, midPoint, endPoint, false);

            leftFork.fork();
            int rightSearchResults = rightFork.compute();
            int leftSearchResults = leftFork.join();

            int finalMinimunmVal = Math.min(rightSearchResults, leftSearchResults);
            return finalMinimunmVal; // Minimum Value Output
        }
    }
}
