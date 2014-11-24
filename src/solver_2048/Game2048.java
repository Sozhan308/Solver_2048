

package solver_2048;
import java.util.*;

public class Game2048 
{
    private int score;
    private int[][] grid;
    private int dimension;
    private int countMoves;
    private int[] lastRandomTilePosition;
    private int lastRandomTileValue;
    private int largestTile;
    private boolean isGameOver;
    static Random randomGenerator=new Random(System.currentTimeMillis());
    Game2048(int n)
    {
        this.dimension=n;
        this.score=0;
        this.countMoves=0;
        this.grid=new int[n][n];
        this.isGameOver=false;
        int i,j;
         for(i=0;i<this.dimension;i++)
        {
           for(j=0;j<this.dimension;j++)
           {
               this.grid[i][j]=0;
           }
        }
        placeRandom();
        largestTile=lastRandomTileValue;
        placeRandom();
        largestTile=Math.max(lastRandomTileValue,largestTile);
    }
    Game2048(Game2048 game)
    {
        this.score=game.score;
        this.countMoves=game.countMoves;
        this.dimension=game.dimension;
        this.grid=new int[this.dimension][this.dimension];
        this.isGameOver=game.isGameOver;
        int i,j;
        for(i=0;i<this.dimension;i++)
        {
           for(j=0;j<this.dimension;j++)
           {
               this.grid[i][j]=game.grid[i][j];
           }
        }
        this.lastRandomTileValue=game.lastRandomTileValue;
        if(game.lastRandomTilePosition!=null)
        {
            this.lastRandomTilePosition=new int[2];
            this.lastRandomTilePosition[0]=game.lastRandomTilePosition[0];
            
            this.lastRandomTilePosition[1]=game.lastRandomTilePosition[1];
        }
        
    }
    public int getDimension()
    {
        return this.dimension;
    }
    public int getCountEmptyTiles()
    {
        int count=0,i,j;
        for(i=0;i<this.dimension;i++)
        {
            for(j=0;j<this.dimension;j++)
            {
                if(grid[i][j]==0)
                    count++;
            }
        }
        return count;
    }
    private Integer[] getRandomEmptySpot()
    {
        int countEmptyCells=this.getCountEmptyTiles();
        if(countEmptyCells==0)
            return null;
        int randomCell=randomGenerator.nextInt(countEmptyCells)+1;
        int i=0,j=0;
        getRandomEmptySpotOuterFor: for(i=0;i<this.dimension;i++)
        {
            for(j=0;j<this.dimension;j++)
            {
                if(grid[i][j]==0)
                {
                    randomCell--;
                    if(randomCell==0)
                        break getRandomEmptySpotOuterFor;
                }
            }
        }
        return new Integer[]{i,j};
    }
    private boolean placeRandom()
    {
        int type=randomGenerator.nextInt(10);
        if(type<9)
            type=2;
        else
            type=4;
        Integer[] place=getRandomEmptySpot();
        if(place==null)
            return false;
      //  System.out.println(place[0]+" "+place[1]);
        placeTileAt(place[0], place[1], type);
        if(lastRandomTilePosition==null)
        {
            lastRandomTilePosition=new int[2];
        }
        lastRandomTilePosition[0]=place[0];
        lastRandomTilePosition[1]=place[1];
        lastRandomTileValue=type;
        largestTile=Math.max(lastRandomTileValue,largestTile);
        isGameOver();
        return true;
    }
    public boolean isGameOver()
    {
        if(!(performMoveLeft(false) || performMoveRight(false) || performMoveUp(false) || performMoveDown(false)))
            isGameOver=true;
        return this.isGameOver;
    }
    private boolean performMoveLeft(boolean makeChanges)
    {
        boolean isChange=false;
        int i;
        for(i=0;i<this.dimension;i++)
        {
            int j1=0,j2=1;
            for(j2=1;j2<this.dimension;)
            {
                if(grid[i][j2]==0)
                {
                    j2++;
                }
                else if(grid[i][j2]==grid[i][j1])
                {
                    isChange=true;
                    if(makeChanges)
                    {
                        grid[i][j1]*=2;
                        this.score+=(grid[i][j1]);
                        grid[i][j2]=0;
                        largestTile=Math.max(grid[i][j1],largestTile);
                    }
                    j2++;
                    j1++;
                }
                else
                {
                    if(grid[i][j1]==0)
                    {
                        isChange=true;
                        if(makeChanges)
                        {
                            grid[i][j1]=grid[i][j2];
                            grid[i][j2]=0;
                        }
                        j2++;
                    }
                    else
                        j1++;
                }
                if(j1==j2)
                {
                    j2++;
                }
            }
                
        }
        if(isChange && makeChanges)
            this.countMoves++;
        return isChange;
    }
    private boolean performMoveRight(boolean makeChanges)
    {
        boolean isChange=false;
        int i;
        for(i=0;i<this.dimension;i++)
        {
            int j1=this.dimension-1,j2=this.dimension-2;
            for(j2=this.dimension-2;j2>=0;)
            {
                if(grid[i][j2]==0)
                {
                    j2--;
                }
                else if(grid[i][j2]==grid[i][j1])
                {
                    isChange=true;
                    if(makeChanges)
                    {
                        grid[i][j1]*=2;
                        this.score+=grid[i][j1];
                        grid[i][j2]=0;
                        largestTile=Math.max(grid[i][j1],largestTile);
                    }
                    j2--;
                    j1--;
                }
                else
                {
                    if(grid[i][j1]==0)
                    {
                        isChange=true;
                        if(makeChanges)
                        {
                            grid[i][j1]=grid[i][j2];
                            grid[i][j2]=0;
                        }
                        j2--;
                    }
                    else
                        j1--;
                }
                if(j1==j2)
                {
                    j2--;
                }
            }
                
        }
        if(isChange && makeChanges)
            this.countMoves++;
        return isChange;
    }
    private boolean performMoveUp(boolean makeChanges)
    {
        boolean isChange=false;
        int j=0;
        for(j=0;j<this.dimension;j++)
        {
            int i1=0,i2=1;
            for(i2=1;i2<this.dimension;)
            {
                if(grid[i2][j]==0)
                {
                    i2++;
                }
                else if(grid[i2][j]==grid[i1][j])
                {
                    isChange=true;
                    if(makeChanges)
                    {
                        grid[i1][j]*=2;
                        this.score+=grid[i1][j];
                        grid[i2][j]=0;
                        largestTile=Math.max(grid[i1][j],largestTile);
                    }
                    i2++;
                    i1++;
                }
                else
                {
                    if(grid[i1][j]==0)
                    {
                        isChange=true;
                        if(makeChanges)
                        {
                            grid[i1][j]=grid[i2][j];
                            grid[i2][j]=0;
                        }
                        i2++;
                    }
                    else
                        i1++;
                }
                if(i2==i1)
                    i2++;
            }
           
                
        }
        if(isChange && makeChanges)
            this.countMoves++;
        return isChange;
    }
    
    private boolean performMoveDown(boolean makeChanges)
    {
        boolean isChange=false;
        int j=0;
        for(j=0;j<this.dimension;j++)
        {
            int i1=this.dimension-1,i2=this.dimension-2;
            for(i2=this.dimension-2;i2>=0;)
            {
                if(grid[i2][j]==0)
                {
                    i2--;
                }
                else if(grid[i2][j]==grid[i1][j])
                {
                    isChange=true;
                    if(makeChanges)
                    {
                        grid[i1][j]*=2;
                        this.score+=(grid[i1][j]);
                        grid[i2][j]=0;
                        largestTile=Math.max(grid[i1][j],largestTile);
                    }
                    i2--;
                    i1--;
                }
                else
                {
                    if(grid[i1][j]==0)
                    {
                        
                        isChange=true;
                        if(makeChanges)
                        {
                            grid[i1][j]=grid[i2][j];
                            grid[i2][j]=0;
                        }
                        i2--;
                    }
                    else
                        i1--;
                }
                if(i1==i2)
                    i2--;
            }
           
        }
        if(isChange && makeChanges)
            this.countMoves++;
        return isChange;
    }
    public int[][] getStatus()
    {
        int[][] currentGame=new int[this.dimension][this.dimension];
        int i,j;
        for(i=0;i<this.dimension;i++)
            for(j=0;j<this.dimension;j++)
                currentGame[i][j]=this.grid[i][j];
        return currentGame;
       
    }
    public int[] getLastRandomTilePosition()
    {
        if(lastRandomTilePosition==null)
            return null;
        return lastRandomTilePosition;
    }
    public Integer getLastRandomTileValue()
    {
        if(lastRandomTilePosition==null)
            return null;
        return lastRandomTileValue;
    }
    public int getLargestTileValue()
    {
        return largestTile;
    }
    public int getScore()
    {
        return score;
    }
    public boolean moveLeft()
    {
        boolean didMove=performMoveLeft(true);
        if(didMove)
            placeRandom();
        return didMove;
    }
    
    public boolean moveRight()
    {
        boolean didMove=performMoveRight(true);
        if(didMove)
            placeRandom();
        return didMove;
    }
    public boolean moveUp()
    {
        boolean didMove=performMoveUp(true);
        if(didMove)
            placeRandom();
        return didMove;
    }
    public boolean moveDown()
    {
        boolean didMove=performMoveDown(true);
        if(didMove)
            placeRandom();
        return didMove;
    }
    public int getCountMoves()
    {
        return this.countMoves;
    }
    public int getTileAt(int x,int y)
    {
        return grid[x][y];
    }
    private boolean placeTileAt(int x,int y,int value)
    {
        if(grid[x][y]!=0)
            return false;
        grid[x][y]=value;
        return true;
    }
    public void printGrid()
    {
        int i,j;
        System.out.println("------------\n\n\n");
        for(i=0;i<grid.length;i++)
        {
            for(j=0;j<grid[i].length;j++)
            {
                if(grid[i][j]==0)
                    System.out.print("_ ");
                else
                    System.out.print(grid[i][j]+" ");
            }
            System.out.println();
        }
    }
    public ArrayList<Game2048> getAllPossibleGameAfterRandomTile()
    {
     //   System.out.println("all possible random tile are");
        ArrayList<Game2048> all=new ArrayList<>();
        int i,j;
        for(i=0;i<this.dimension;i++)
        {
            for(j=0;j<this.dimension;j++)
            {
                if(this.getTileAt(i, j)==0)
                {
                    Game2048 temp=new Game2048(this);
                    temp.placeTileAt(i, j, 2);
              //      if(temp.isGameOver()==false)
                        all.add(temp);
       //             temp.printGrid();
                    temp=new Game2048(this);
                    temp.placeTileAt(i,j,4);
                //    if(temp.isGameOver()==false)
                        all.add(temp);
         //           temp.printGrid();
                }
            }
        }
        //System.out.println("OVER");
        return all;
        
    }
    public ArrayList<Game2048> getAllPossibleGamesAfterLeftMove()
    {
        
        Game2048 afterLeft=new Game2048(this);
        if(afterLeft.performMoveLeft(true)==false)
            return new ArrayList<Game2048>();
      
        return afterLeft.getAllPossibleGameAfterRandomTile();        
        
    }
    public ArrayList<Game2048> getAllPossibleGamesAfterRightMove()
    {
       
        Game2048 afterRight=new Game2048(this);
        if(afterRight.performMoveRight(true)==false)
            return new ArrayList<Game2048>();
       
        return afterRight.getAllPossibleGameAfterRandomTile();        
        
    }
    public ArrayList<Game2048> getAllPossibleGamesAfterUpMove()
    {
        Game2048 afterUp=new Game2048(this);
        if(afterUp.performMoveUp(true)==false)
            return new ArrayList<Game2048>();
        return afterUp.getAllPossibleGameAfterRandomTile();        
        
    }
    public ArrayList<Game2048> getAllPossibleGamesAfterDownMove()
    {
        Game2048 afterDown=new Game2048(this);
        if(afterDown.performMoveDown(true)==false)
            return new ArrayList<Game2048>();
        return afterDown.getAllPossibleGameAfterRandomTile();        
        
    }
    public boolean isLeftPossible()
    {
        return performMoveLeft(false);
    }
    public boolean isRightPossible()
    {
        return performMoveRight(false);
    }
    public boolean isUpPossible()
    {
        return performMoveUp(false);
    }
    public boolean isDownPossible()
    {
        return performMoveDown(false);
    }
}
