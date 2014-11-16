package solver_2048;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;


public class Solver_2048 
{
    private static int weight_gameScore=1,weight_emptyTileScore=1,weight_clusterScore=0,
            weight_largestTileScore=0,weight_gradientScore=1;
    static private final int UP=0,DOWN=1,LEFT=2,RIGHT=3;
    static private final double Epsilon=0.000001;
 
    static private class Node
    {
       private Game2048 game;
       private boolean areChildNodesGenerated;
       private ArrayList<Node> allAfterLeftMove,allAfterRightMove,allAfterUpMove,allAfterDownMove;
       private Double probableScoreAfterLeftMove,probableScoreAfterRightMove,probableScoreAfterUpMove,probableScoreAfterDownMove;
       private Double clusterScore,emptyTileScore,gameScore,largestTileScore,gradientScore;
       Node(Game2048 game)
       {
           this.allAfterDownMove=new ArrayList<>();
           this.allAfterLeftMove=new ArrayList<>();
           this.allAfterRightMove=new ArrayList<>();
           this.allAfterUpMove=new ArrayList<>();
            probableScoreAfterDownMove=0.0;
            probableScoreAfterLeftMove=0.0;
            probableScoreAfterRightMove=0.0;
            probableScoreAfterUpMove=0.0;
           this.game=new Game2048(game);
           this.areChildNodesGenerated=false;
           this.largestTileScore=new Double(this.game.getLargestTileValue());
           gameScore=new Double(game.getScore());
           if(gameScore>10)
            emptyTileScore=Math.min(0,game.getCountEmptyTiles() *( Math.log10(gameScore)/Math.log10(2)));
           else
            emptyTileScore=new Double(game.getCountEmptyTiles());
           emptyTileScore=Math.pow(emptyTileScore, 1);
        
           computeClusterScore();
           if(gameScore>10)
                this.clusterScore*=Math.log10(gameScore);
           computeGradientScore();
           if(gameScore>10)
                this.gradientScore*=Math.log10(gameScore);
       }
       private boolean isEqualDouble(Double one,Double two)
       {
           if(Math.abs(one-two)<=Epsilon)
               return true;
           return false;
       }
       private void printarr(int[][] arr)
       {
           int i,j;
           for(i=0;i<arr.length;i++)
           {
               for(j=0;j<arr[i].length;j++)
               {
                   System.out.print(arr[i][j]+" ");
               }
               System.out.println();
           }
           System.out.println();
       }
       
      
       private Integer getHorizontalGradientScore(int istart,int idiff,int noiter,int jdir)
       {
            int[][] grid=game.getStatus();
           int score=0;
           
           int n=game.getDimension();
           int mat[][]=new int[n][n];
           int i,j,begin=n*noiter,iiter;
       
           for(i=istart,iiter=0;i>=0 && i<n && iiter<noiter;i+=idiff,iiter++)
           {
               if(i%2==jdir)
                   for(j=0;j<n;j++)
                   {
                       score+=(grid[i][j]*begin);
                       mat[i][j]=begin;
                       begin--;
                   }
               else
                   for(j=n-1;j>=0;j--)
                   {
                       score+=(grid[i][j]*begin);
                       mat[i][j]=begin;
                       begin--;
                   }
           }
           //printarr(mat);
           return score;
       }
       private Integer getVerticalGradientScore(int jstart,int jdiff,int noiter,int idir)
       {
            int[][] grid=game.getStatus();
           int score=0;
           
           int n=game.getDimension();
           int mat[][]=new int[n][n];
           int i,j,begin=n*noiter,jiter;
       
           for(j=jstart,jiter=0;j>=0 && j<n && jiter<noiter;j+=jdiff,jiter++)
           {
               if(j%2==idir)
                   for(i=0;i<n;i++)
                   {
                       score+=(grid[i][j]*begin);
                       mat[i][j]=begin;
                       begin--;
                   }
               else
                   for(i=n-1;i>=0;i--)
                   {
                       score+=(grid[i][j]*begin);
                       mat[i][j]=begin;
                       begin--;
                   }
           }
           //printarr(mat);
           return score;
       }
       private void computeGradientScore()
       {
           int[][] grid=game.getStatus();
           int n=game.getDimension();
           int[][] mat=new int[n][n];
           int i,j;
           long score1=0,score2=0,score3=0,score4=0,score5=0,score6=0,score7=0,score8=0;
           score1=getHorizontalGradientScore(0, 1, n/2+1 , 0);
           score2=getHorizontalGradientScore(0, 1, n/2+1, 1);
           score3=getHorizontalGradientScore(n-1, -1, n/2+1, 0);
           score4=getHorizontalGradientScore(n-1, -1, n/2+1, 1);
           
           score5=getVerticalGradientScore(0, 1, n/2+1, 0);
           score6=getVerticalGradientScore(0, 1, n/2+1, 1);
           score7=getVerticalGradientScore(n-1, -1, n/2+1, 0);
           score8=getVerticalGradientScore(n-1, -1, n/2+1, 1);
         
           gradientScore=new Double(Math.max(score1,Math.max(score2,Math.max(score3,Math.max(score4,Math.max(score5,Math.max(score6,Math.max(score7,score8))))))));
           
           //System.out.println(score1+" "+score2+" "+score3+" "+score4+" "+score5+" "+score6+" "+score7+" "+score8+" "+gradientScore);
           if(gameScore>10)
            gradientScore*=Math.log(gameScore);
       }
       
       private void computeClusterScore()
       {
               int[][] grid=game.getStatus();
               int n=game.getDimension();
           int i,j,i1,j1;
           clusterScore=0.0;
           
           for(i=0;i<n;i++)
           {
               for(j=0;j<n;j++)
               {
                   double thisone=0;
                   int count=0;
                   if(grid[i][j]==0)
                       continue;
                   for(i1=i-1;i1<=i+1;i1++)
                   {
                       for(j1=j-1;j1<=j+1;j1++)
                       {
                           if((i1==i && j1==j)|| !(i1>=0 && j1>=0 && i1<n && j1<n) )
                               continue;
                           if(grid[i1][j1]==0)
                               continue;
                           count++;
                           thisone=Math.pow(Math.abs(grid[i][j]-grid[i1][j1]),1);//TRY squaring it
                       }
                   }
              
                   if(count!=0)
                    clusterScore+=((double)(thisone)/count);//try without averaging too
                 //clusterScore+=thisone;
               }
           }
           if(gameScore>10)
            clusterScore*=Math.log10(gameScore);
       }
       private boolean isCornerTile(int a,int b)
       {
           if((a==0 && b==0) || (a==0 && b==this.game.getDimension()-1) || (a==this.game.getDimension()-1 && b==0) || (a==this.game.getDimension()-1 && b==this.game.getDimension()-1))
                return true;
           return false;
       }
       private boolean isEdgeTile(int a,int b)
       {
           if(a==0 || b==0 || (a==this.game.getDimension()-1) || (b==this.game.getDimension()-1))
               return true;
           return false;
       }
    
       private void generateChildNodes()
       {
           ArrayList<Game2048> temp;
           temp=this.game.getAllPossibleGamesAfterLeftMove();
           for(int i=0;i<temp.size();i++)
              //  if(temp.get(i).getLastRandomTileValue()==2)
                  allAfterLeftMove.add(new Node(temp.get(i)));
      
           temp=this.game.getAllPossibleGamesAfterRightMove();
           for(int i=0;i<temp.size();i++)
               //if(temp.get(i).getLastRandomTileValue()==2)
                   allAfterRightMove.add(new Node(temp.get(i)));
           
           
           temp=this.game.getAllPossibleGamesAfterUpMove();
           for(int i=0;i<temp.size();i++)
        //       if(temp.get(i).getLastRandomTileValue()==2)
                  allAfterUpMove.add(new Node(temp.get(i)));
           
           temp=this.game.getAllPossibleGamesAfterDownMove();
           for(int i=0;i<temp.size();i++)
          //     if(temp.get(i).getLastRandomTileValue()==2)
                 allAfterDownMove.add(new Node(temp.get(i)));
      /*     
           if(allAfterLeftMove.size()==0)
           {
               temp=this.game.getAllPossibleGamesAfterLeftMove();
                for(int i=0;i<temp.size();i++)
                 if(temp.get(i).getLastRandomTileValue()==4)
                  allAfterLeftMove.add(new Node(temp.get(i), this.gameScore));
           }
           if(allAfterDownMove.size()==0)
           {
            temp=this.game.getAllPossibleGamesAfterDownMove();
           for(int i=0;i<temp.size();i++)
               if(temp.get(i).getLastRandomTileValue()==4)
                 allAfterDownMove.add(new Node(temp.get(i), this.gameScore));    
           }
           if(allAfterUpMove.size()==0)
           {
                temp=this.game.getAllPossibleGamesAfterUpMove();
           for(int i=0;i<temp.size();i++)
               if(temp.get(i).getLastRandomTileValue()==4)
                  allAfterUpMove.add(new Node(temp.get(i), this.gameScore));
           }
           if(allAfterRightMove.size()==0)
           {
                temp=this.game.getAllPossibleGamesAfterRightMove();
           for(int i=0;i<temp.size();i++)
               if(temp.get(i).getLastRandomTileValue()==2)
                   allAfterRightMove.add(new Node(temp.get(i), this.gameScore));
           }*/
               
           
           
           areChildNodesGenerated=true;
           
       }
       public double getScore()
       {
           return Math.max(0,weight_gameScore*gameScore  +  weight_emptyTileScore*emptyTileScore  +   weight_largestTileScore*largestTileScore + 
                   + gradientScore*weight_gradientScore- weight_clusterScore*clusterScore);
       }
       public Integer getNextBestMove(int maxDepth)
       {
           assert (maxDepth>=1);
           for(int i=maxDepth;i>=1;i--)
           {
           //       System.out.println("Depth :"+i);
                Double bestScore=getNextBestScore(i);
                if(isEqualDouble(bestScore,0.0))
                    continue;
    
             /*   if(isEqualDouble(probableScoreAfterDownMove,0.0))
                {
                    System.out.println("DOWN MOVE ZERO : "+this.allAfterDownMove.size());
                }
                if(isEqualDouble(probableScoreAfterUpMove,0.0))
                {
                    System.out.println("Up MOVE ZERO : "+this.allAfterUpMove.size());
                }
                if(isEqualDouble(probableScoreAfterLeftMove,0.0))
                {
                    System.out.println("Left MOVE ZERO : "+this.allAfterLeftMove.size());
                }
                if(isEqualDouble(probableScoreAfterRightMove,0.0))
                {
                    System.out.println("Right MOVE ZERO : "+this.allAfterRightMove.size());
                }
                System.out.println("LEFT : "+probableScoreAfterLeftMove+" RIGHT : "+probableScoreAfterRightMove+"UP: "+probableScoreAfterUpMove+" DOWN :"+probableScoreAfterDownMove);*/
                if(isEqualDouble(bestScore,probableScoreAfterLeftMove))
                    return LEFT;
                else if(isEqualDouble(bestScore,probableScoreAfterRightMove))
                    return RIGHT;
                else if(isEqualDouble(bestScore,probableScoreAfterUpMove))
                    return UP;
                else if(isEqualDouble(bestScore,probableScoreAfterDownMove))
                    return DOWN;
           }
           return null;
               
           
           
       }
       public double getNextBestScore(int atDepth)
       {
           if(atDepth==0)
               return this.getScore();
            if(!areChildNodesGenerated)
               generateChildNodes();
          
        
            Double probabilityOfThis,emptyTiles;
           
            for(int i=0;i<allAfterLeftMove.size();i++)
            {
                emptyTiles=new Double(allAfterLeftMove.get(i).game.getCountEmptyTiles()+1);
                if(allAfterLeftMove.get(i).game.getLastRandomTileValue()==2)
                    probabilityOfThis=0.9*(1/emptyTiles);
                else
                    probabilityOfThis=0.1*(1/emptyTiles);
                probableScoreAfterLeftMove+=(probabilityOfThis*allAfterLeftMove.get(i).getNextBestScore(atDepth-1));
            }
            
            for(int i=0;i<allAfterRightMove.size();i++)
            {
                emptyTiles=new Double(allAfterRightMove.get(i).game.getCountEmptyTiles()+1);
                if(allAfterRightMove.get(i).game.getLastRandomTileValue()==2)
                    probabilityOfThis=0.9*(1/emptyTiles);
                else
                    probabilityOfThis=0.1*(1/emptyTiles);
                probableScoreAfterRightMove+=(probabilityOfThis*allAfterRightMove.get(i).getNextBestScore(atDepth-1));
            }
                 
            for(int i=0;i<allAfterUpMove.size();i++)
            {
                emptyTiles=new Double(allAfterUpMove.get(i).game.getCountEmptyTiles()+1);
                if(allAfterUpMove.get(i).game.getLastRandomTileValue()==2)
                    probabilityOfThis=0.9*(1/emptyTiles);
                else
                    probabilityOfThis=0.1*(1/emptyTiles);
                probableScoreAfterUpMove+=(probabilityOfThis*allAfterUpMove.get(i).getNextBestScore(atDepth-1));
            }
                      
            for(int i=0;i<allAfterDownMove.size();i++)
            {
                emptyTiles=new Double(allAfterDownMove.get(i).game.getCountEmptyTiles()+1);
                if(allAfterDownMove.get(i).game.getLastRandomTileValue()==2)
                    probabilityOfThis=0.9*(1/emptyTiles);
                else
                    probabilityOfThis=0.1*(1/emptyTiles);
                probableScoreAfterDownMove+=(probabilityOfThis*allAfterDownMove.get(i).getNextBestScore(atDepth-1));
            }
            
          // System.out.println("got "+probableScoreAfterLeftMove+" "+probableScoreAfterRightMove+" "+probableScoreAfterUpMove+" "+probableScoreAfterDownMove);
           return Math.max(probableScoreAfterDownMove,Math.max(probableScoreAfterLeftMove,Math.max(probableScoreAfterRightMove,probableScoreAfterUpMove)));
       }
       
    }
    private static int countDigits(int n)
    {
        int a=0;
        while(n>0)
        {
            a++;
            n/=10;
        }
        return a;
    }
    public static void print(Game2048 game,PrintStream writer)
    {
        writer.println("\n\n___________________________________\n\n");
        int[][] arr=game.getStatus();
        int i,j;
        int space=4;
 
        for(i=0;i<arr.length;i++)
        {
            for(j=0;j<arr[i].length;j++)
            {
                writer.print(" ");
                if(arr[i][j]==0)
                    for(int k=0;k<space;k++)
                        writer.print("-");
                else
                {   
                    int digits=countDigits(arr[i][j]);
                    
                    writer.print(arr[i][j]);
                    digits=space-digits;
                    while(digits>0)
                    {
                        writer.print(" ");
                        digits--;
                    }
                }
                writer.print(" ");
            }
            writer.println("");
        }
       
        writer.println("\n\n");
        
    }
    public static void main(String[] args) throws IOException {
        
        Scanner in =new Scanner(System.in);
        File file = new File("game");
        file.createNewFile();
        
        PrintStream writer = new PrintStream(System.out);

        Game2048 game=new Game2048(4);
        
        print(game,writer);
        long starttime=System.currentTimeMillis();
        long lastTime=starttime,currentTime;
        while(game.isGameOver()==false )//&& game.getLargestTileValue()!=2048)
        {
            System.out.println(game.getCountMoves());
            Node currentGame=new Node(game);
            int nextBestMove=currentGame.getNextBestMove(2);
            currentTime=System.currentTimeMillis();
            //nextBestMove=in.nextInt();
              
            if(nextBestMove==DOWN)
            {
               
                game.moveDown();
            }
            else if(nextBestMove==UP)
            {
              
                game.moveUp();
            }
            else if(nextBestMove==LEFT)
            {
             
                game.moveLeft();
            }
            else if(nextBestMove==RIGHT)
            {
              
                game.moveRight();
            }
             int[] randomTilePlace=game.getLastRandomTilePosition();
        int randomTileValue=game.getLastRandomTileValue();
       
      
            print(game,writer);
            if(nextBestMove==DOWN)
            {
                writer.println("Last Move: DOWN");
              
            }
            else if(nextBestMove==UP)
            {
                writer.println("Last Move: UP");
              
            }
            else if(nextBestMove==LEFT)
            {
                writer.println("Last Move: LEFT");
              
            }
            else if(nextBestMove==RIGHT)
            {
                writer.println("Last Move: RIGHT");
              
            }
           
          //  System.out.println("Move Count: "+(game.getCountMoves())+" \nMaximum Tile :"+game.getLargestTileValue()); 
            
              writer.println("Move Count: "+game.getCountMoves()+" \nMaximum Tile :"+game.getLargestTileValue()+"\nScore: "+game.getScore()); 
                writer.println("Random tile "+randomTileValue+" at  "+randomTilePlace[0]+","+randomTilePlace[1]+"\nTime since beginning :"+(double)(currentTime-starttime)/1000+" seconds \nTime for this move: "+(double)(currentTime-lastTime)/1000+"seconds");
        lastTime=currentTime;
       // in.nextInt();
       
        }
    }
}
