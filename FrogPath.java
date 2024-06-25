public class FrogPath {
    private Pond pond;

    public FrogPath(String filename){
        try {
            pond = new Pond(filename);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String findPath() {
        ArrayStack<Hexagon> stack = new ArrayStack<>();
        Hexagon start = pond.getStart();
        stack.push(start);
        start.markInStack();
    
        int fliesEaten = 0;
        boolean foundSolution = false; // Flag to track if a solution is found
        StringBuilder pathString = new StringBuilder();
    
        while (!stack.isEmpty()) {
            Hexagon curr = stack.peek();
            
            pathString.append(curr.getID()).append(" ");
    
            if (curr.isEnd()) {
                foundSolution = true;
                break;
            }
    
            if (curr instanceof FoodHexagon) {
                fliesEaten += ((FoodHexagon) curr).getNumFlies();
                ((FoodHexagon) curr).removeFlies();
            }
    
            Hexagon next = findBest(curr);
            if (next == null) {
                stack.pop();
                curr.markOutStack();
            } else {
                stack.push(next);
                next.markInStack();
            }
        }
    
        if (!foundSolution) {
            return "No solution";
        } else {
            pathString.append("ate ").append(fliesEaten).append(" flies");
            return pathString.toString();
        }
    }
    

    public Hexagon findBest(Hexagon currCell) {
        ArrayUniquePriorityQueue<Hexagon> queue = new ArrayUniquePriorityQueue<>();
        
        // Add adjacent cells to the priority queue
        addAdjacentCellsToQueue(currCell, queue);
        
        // If currCell is a lilypad, also add 2-away cells to the queue
        if (currCell.isLilyPadCell()) {
            addTwoAwayCellsToQueue(currCell, queue);
        }
        
        Hexagon bestCell = getBestCell(queue, currCell);
        
        // Check if the bestCell is adjacent to an alligator, and if so, set bestCell to null
        if (bestCell != null && isAdjacentToAlligator(bestCell)) {
            bestCell = null;
        }
        
        return bestCell;
    }

    private void addAdjacentCellsToQueue(Hexagon currCell, ArrayUniquePriorityQueue<Hexagon> queue) {
        for (int i = 0; i < 6; i++) {
            try {
                Hexagon neighbour = currCell.getNeighbour(i);
                if (neighbour != null && !neighbour.isMarked() && !neighbour.isMudCell() && !neighbour.isAlligator()) {
                    if (neighbour.isReedsCell() || !isAdjacentToAlligator(neighbour)) {
                        double priority = calculatePriority(currCell, neighbour);
                        queue.add(neighbour, priority);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            for (int j = 0; i < 6; i++) {
                try {
                    Hexagon neighbour = currCell.getNeighbour(j);
                    if (neighbour != null && !neighbour.isMarked()) {
                        Hexagon nextNeighbour = neighbour.getNeighbour(j);
                        if (nextNeighbour != null && !nextNeighbour.isMarked() && !nextNeighbour.isAlligator()) {
                            if (nextNeighbour.isReedsCell() || !isAdjacentToAlligator(nextNeighbour)) {
                                double priority = calculatePriority(currCell, nextNeighbour);
                                queue.add(nextNeighbour, priority);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void addTwoAwayCellsToQueue(Hexagon currCell, ArrayUniquePriorityQueue<Hexagon> queue) {
        for (int i = 0; i < 6; i++) {
            try {
                Hexagon neighbour = currCell.getNeighbour(i);
                if (neighbour != null && !neighbour.isMarked()) {
                    Hexagon nextNeighbour = neighbour.getNeighbour(i);
                    if (nextNeighbour != null && !nextNeighbour.isMarked() && !nextNeighbour.isAlligator()) {
                        if (nextNeighbour.isReedsCell() || !isAdjacentToAlligator(nextNeighbour)) {
                            double priority = calculatePriority(currCell, nextNeighbour);
                            queue.add(nextNeighbour, priority);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private double calculatePriority(Hexagon currCell, Hexagon neighbour) {
        double basePriority = getBasePriority(neighbour);
        double distancePriority = getDistancePriority(currCell, neighbour);

        // Adjust priority based on whether the neighbour is marked or not
        double markedPriority = neighbour.isMarked() ? 100.0 : 0.0;
        //System.out.print(basePriority + distancePriority + markedPriority + " ");
        //System.out.println(neighbour);
        return basePriority + distancePriority + markedPriority;
    }

    private double getBasePriority(Hexagon hexagon) {
        if (hexagon instanceof FoodHexagon) {
            int numFlies = ((FoodHexagon) hexagon).getNumFlies();
            if (numFlies == 3) {
                return 0.0; // Priority for 3 flies
            } else if (numFlies == 2) {
                return 1.0; // Priority for 2 flies
            } else if (numFlies == 1) {
                return 2.0; // Priority for 1 fly
            }
        } else if (hexagon.isEnd()) {
            return 3.0; // Priority for the end cell
        } else if (hexagon.isLilyPadCell()) {
            return 4.0; // Priority for lilypad cells
        } else if (hexagon.isReedsCell()) {
            return 5.0; // Priority for reeds cells
        } else if (hexagon.isWaterCell()){
            return 6.0; // Priority for water cells
        } else if (hexagon.isAlligator()) {
            return 10.0; // Avoid alligators
        } else if (hexagon.isMudCell()) {
            return 10.0; // Avoid mud cells
        }
        return 10.0; // Default priority for unknown cells
    }

    private double getDistancePriority(Hexagon currCell, Hexagon neighbour) {
        int distance = distanceTo(currCell, neighbour);
        if (distance == 2 && isAligned(currCell, neighbour)) {
            return 0.5; // Adjust priority for cells 2 away in a straight line
        } else if (distance == 2 && !isAligned(currCell, neighbour)) {
            return 1.0; // Adjust priority for cells 2 away not in a straight line
        } else {
            return 0.0; // Default distance priority
        }
    }

    private boolean isAligned(Hexagon currCell, Hexagon neighbour) {
        int currID = currCell.getID();
        int neighbourID = neighbour.getID();
        return (currID + 1 == neighbourID && currID % 3 != 2) ||
                (currID - 1 == neighbourID && currID % 3 != 0);
    }

    private int distanceTo(Hexagon currCell, Hexagon neighbour) {
        int currID = currCell.getID();
        int neighbourID = neighbour.getID();
        return Math.abs(currID - neighbourID);
    }

    private boolean isAdjacentToAlligator(Hexagon currCell) {
        for (int i = 0; i < 6; i++) {
            try {
                Hexagon neighbour = currCell.getNeighbour(i);
                if (neighbour != null && neighbour.isAlligator()) {
                    return true; // Found an adjacent alligator
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false; // No adjacent alligator found
    }

    private Hexagon getBestCell(ArrayUniquePriorityQueue<Hexagon> queue, Hexagon currCell) {
        Hexagon bestCell = null;
        double lowestPriority = Double.MAX_VALUE;
        boolean foundBestCell = false; // Flag to track if the best cell has been found
    
        while (!queue.isEmpty() && !foundBestCell) {
            Hexagon currentCell = null;
            try {
                currentCell = queue.removeMin(); // Remove the cell with the lowest priority from the queue
            } catch (Exception e) {
                e.printStackTrace(); // Handle the exception if needed
            }
    
            // Skip marked cells
            if (currentCell.isMarked()) {
                continue;
            }
    
            double basePriority = getBasePriority(currentCell);
            double distancePriority = getDistancePriority(currCell, currentCell);
            double markedPriority = currentCell.isMarked() ? 100.0 : 0.0;
            double priority = basePriority + distancePriority + markedPriority;
    
            if (priority < lowestPriority) {
                bestCell = currentCell;
                lowestPriority = priority;
            }
    
            // If cells have the same priority, the first one looked at is considered the best path
            
        }
    
        return bestCell;
    }
    
    
}
