import java.util.*;

class Routine implements Comparable <Routine> {
    private int[][] locations;
    private int[] order;

    private double sumLargerDistances;

    public Routine(int[][] locations) {
        this.locations = locations;
        order = new int[locations.length];
        for(int i=0; i< locations.length; i++) {
            order[i] = i;
        }

        sumLargerDistances = getSumLargerDistances();
    }

    public Routine(int[][] locations, int[] order) {
        this.locations = locations;
        this.order = order;
        sumLargerDistances = getSumLargerDistances();
    }

    public int getX(int i) {
        return locations[order[i]][0];
    }

    public int getY(int i) {
        return locations[order[i]][1];
    }

    public int getOrder(int i) {
        return 	order[i];
    }

    public void setOrder(int i, int value) {
        this.order[i] = value;
    }

    public double getSumLargerDistances() {
        double sum = 0;
        for(int i=0; i< locations.length-1; i++) {
            int dx = Math.abs(locations[order[i]][0]- locations[order[i+1]][0]);
            int dy = Math.abs(locations[order[i]][1]- locations[order[i+1]][1]);
            if( dx > dy) {
                sum += dx;
            }
            else {
                sum += dy;
            }
        }
        return sum;
    }

    public int getSize() {
        return locations.length;
    }

    public Routine copyRoutine() {
        int[] temp_order = new int[order.length];
        for(int i=0; i<order.length; i++) {
            temp_order[i] = getOrder(i);
        }
        return new Routine(locations, temp_order);
    }

    public void mutate() {
        Random r = new Random();
        int swap1 = r.nextInt(order.length-1)+1;
        int swap2 = r.nextInt(order.length-1)+1;
        int temp = order[swap1];
        order[swap1] = order[swap2];
        order[swap2] = temp;
        sumLargerDistances = getSumLargerDistances();
    }


    public void mutate_insertion() {
        Random r = new Random();
        int element = r.nextInt(order.length-2)+1;
        int insert_after = r.nextInt(order.length-2)+1;

        if(element != insert_after) {
            //create an array with element removed
            int element_copy = order[element];

            int[] removed = new int[order.length-1];
            for(int i=0; i<element;i++){
                removed[i] = order[i];
            }

            for(int i=element; i< order.length-1; i++){
                removed[i] = order[i+1];
            }

            //create an array with element inserted
            for(int i=1; i<insert_after;i++){
                order[i] = removed[i];
            }
            order[insert_after] = element_copy;

            for(int i=insert_after+1; i<order.length-1; i++){
                order[i] = removed[i-1];
            }
        }
        sumLargerDistances = getSumLargerDistances();
    }


    //replace a chuck of the female routine with a chunk of the male routine.
    //store the values being replace in the location of the incoming indexes
    public Routine mate(Routine Male) {

        Routine Baby = this.copyRoutine();

        Random r = new Random();

        int startMale = r.nextInt(order.length-2)+1;
        int length = r.nextInt(order.length-startMale-1);
        int startFemale = r.nextInt(order.length-length);
        int stopping_point = startMale+length;

        for(int i=startFemale; i < stopping_point; i++) {
            int incoming = Male.getOrder(i);
            int outgoing = Baby.getOrder(i);
            for(int j=0; j<order.length; j++) {
                if(Baby.getOrder(j) == incoming) {
                    Baby.setOrder(j, outgoing);
                    break;
                }
            }
            Baby.setOrder(i, incoming);
        }

        return Baby;
    }

    public int compareTo(Routine b) {
        if(this.sumLargerDistances <  b.sumLargerDistances) {
            return -1;
        }
        else {
            return 1;
        }
    }


    public int getMaxX() {
        int maxX = -1;
        for(int i=0; i< locations.length; i++)  {
            if(locations[i][0] > maxX) {
                maxX = locations[i][0];
            }
        }
        return maxX;
    }

    public int getMaxY()  {
        int maxY = -1;
        for(int i=0; i< locations.length; i++) {
            if(locations[i][1] > maxY) {
                maxY = locations[i][1];
            }
        }
        return maxY;
    }


    public void randomize(int n) {
        for(int i=0; i<n; i++) {
            Random r = new Random();
            int swap1 = r.nextInt(locations.length);
            int swap2 = r.nextInt(locations.length);

            int temp = order[swap1];
            order[swap1] = order[swap2];
            order[swap2] = temp;
        }
        sumLargerDistances = getSumLargerDistances();
    }


    public void easyHeuristic()  {

        int[] newSolution = new int[order.length];

        newSolution[0] = order[0];
        newSolution[1] = order[order.length-1];

        //add every position to path length
        for(int i=1; i< order.length-1; i++) {
            //copy last point into next array position
            newSolution[i+1] = newSolution[i];

            //copy new point into array, just before the last element
            newSolution[i] = order[i];

            //initialize with current positioning as best
            int bestPosition = i;
            double bestLength = getSumLargerDistances(locations, newSolution, i+2);

            //swap new element into all possibilities
            for(int j=i; j>1; j--) {
                int temp = newSolution[j];
                newSolution[j] = newSolution[j-1];
                newSolution[j-1] = temp;

                double test_length = getSumLargerDistances(locations, newSolution, i+2);
                if(test_length < bestLength) {
                    bestLength = test_length;
                    bestPosition = j-1;
                }
            }

            //roll back swaps till best length is back
            for(int j=1; j<bestPosition; j++)  {
                int temp = newSolution[j];
                newSolution[j] = newSolution[j+1];
                newSolution[j+1] = temp;
            }
        }

        this.order = newSolution;
        sumLargerDistances = getSumLargerDistances();
    }

    public static double getSumLargerDistances(int[][] out_locations, int[] order, int size) {
        double sum = 0;
        for(int i=0; i<size-1; i++) {
            int dx = Math.abs(out_locations[order[i]][0]-out_locations[order[i+1]][0]);
            int dy = Math.abs(out_locations[order[i]][1]-out_locations[order[i+1]][1]);

            if( dx > dy)  {
                sum += dx;
            }
            else {
                sum += dy;
            }

            /* If you can't travel two axes simultaneosly, use distance equation
            sum += Math.sqrt(dx*dx+dy*dy);
            */

        }
        return sum;
    }



    public void mutate_branch_swap(){
        Random r = new Random();

        int swap1 = r.nextInt(order.length);
        int swap2 = r.nextInt(order.length);

        int start = Math.min(swap1, swap2);
        int stop =  Math.max(swap1, swap2);

        if(stop-start > 1) {
            int[] swapper = new int[stop-start+1];
            int swapper_count = 0;
            for(int i=stop; i>(start-1); i--) {
                swapper[swapper_count]	= order[i];
                swapper_count++;
            }

            swapper_count = 0;
            for(int i=start; i<(stop+1); i++) {
                order[i] = swapper[swapper_count];
                swapper_count++;
            }
        }
        sumLargerDistances = getSumLargerDistances();
    }

    public int[][] getLocations() {
        int[][] out_locations = new int[order.length][2];

        for(int i=0; i < order.length; i++) {
            out_locations[i][0] = locations[order[i]][0];
            out_locations[i][1] = locations[order[i]][1];
        }

        return out_locations;
    }

    public void thoroughHeuristic() {
        int max_x = locations[0][0];
        int max_y = locations[0][1];
        int min_x = locations[0][0];
        int min_y = locations[0][1];

        for(int i=1; i<order.length; i++) {
            if(locations[i][0] > max_x)
                max_x = locations[i][0];

            if(locations[i][1] > max_y)
                max_y = locations[i][1];

            if(locations[i][0] < min_x)
                min_x = locations[i][0];

            if(locations[i][1] < min_y)
                min_y = locations[i][0];
        }

        float x_center = (max_x - min_x)/2;
        float y_center = (max_y - min_y)/2;

        //calculate the distance from each element from center
        double[] distances = new double[order.length];

        for(int i=0; i<order.length; i++) {
            float dx = locations[order[i]][0] - x_center;
            float dy = locations[order[i]][1] - y_center;

            distances[i] = Math.sqrt(dx*dx + dy*dy);
        }

        //sort order by distance from center - bubble sort

        for (int i=0; i<order.length-1; i++) {
            for (int j=0; j<order.length-1-i; j++) {
                if (distances[j+1] > distances[j]) {  /* compare the two neighbors */
                    double tmp = distances[j];         /* swap a[j] and a[j+1]      */
                    distances[j] = distances[j+1];
                    distances[j+1] = tmp;

                    int tmp1 = order[j];         /* swap a[j] and a[j+1]      */
                    order[j] = order[j+1];
                    order[j+1] = tmp1;
                }
            }
        }

        int[] newSolution = new int[order.length];

        newSolution[0] = order[0];
        newSolution[1] = order[1];

        //add every hole to path length
        for(int i=2; i< order.length; i++){

            //copy new point into array, as the last element
            newSolution[i] = order[i];

            //initialize with current positioning as best
            int bestPosition = i;
            double bestLength = getSumLargerDistances(locations, newSolution, i+1);

            //swap new element into all possibilities
            for(int j=i; j>0; j--) {
                int temp = newSolution[j];
                newSolution[j] = newSolution[j-1];
                newSolution[j-1] = temp;

                double test_length = getSumLargerDistances(locations, newSolution, i+1);
                if(test_length < bestLength){
                    bestLength = test_length;
                    bestPosition = j-1;
                }
            }

            //roll back swaps till best length is back
            for(int j=0; j<bestPosition; j++) {
                int temp = newSolution[j];
                newSolution[j] = newSolution[j+1];
                newSolution[j+1] = temp;
            }
        }

        this.order = newSolution;
        sumLargerDistances = getSumLargerDistances();
    }
}
