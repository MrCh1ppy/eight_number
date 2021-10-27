package com.ch1ppy.eight_number.dealer;
import lombok.Data;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Data
public class EightNumberHelper{
    private int[] num = new int[9];
    private int evaluation;                //估计函数f(n)：从起始状态到目标的最小估计值
    private int depth;                    //d(n)：当前的深度，即走到当前状态的步骤
    private int misPosition;            //启发函数 h(n)：到目标的最小估计(记录和目标状态有多少个数不同)
    private EightNumberHelper parent;            //当前状态的父状态
    private List<EightNumberHelper> answer = new ArrayList<>();    //保存最终路径

    /**
     * 判断当前状态是否为目标状态
     */
    private boolean isTarget(@NotNull EightNumberHelper target){
        return Arrays.equals(getNum(), target.getNum());
    }

    /**
     * 求估计函数f(n) = g(n)+h(n);
     * 估值函数用当前步数+当前差值
     * 数据越小越优秀
     * 初始化状态信息
     */
    private void init(EightNumberHelper target){
        int temp = 0;
        for(int i=0;i<9;i++){
            if(num[i]!=target.getNum()[i]){
                temp++; //记录当前节点与目标节点差异的度量
            }
        }
        this.setMisPosition(temp);
        if(this.getParent()==null){
            this.setDepth(0);    //初始化步数（深度）
        }else{
            this.depth = this.parent.getDepth()+1;//记录步数
        }
        this.setEvaluation(this.getDepth()+this.getMisPosition());//返回当前状态的估计值
    }

    /**
     * 求逆序值并判断是否有解，逆序值同奇或者同偶才有解
     * @return 有解：true 无解：false
     */
    private boolean isSolvable(EightNumberHelper target){
        int reverse = 0;
        for(int i=0;i<9;i++){
            for(int j=0;j<i;j++){//遇到0跳过
                if(num[j]>num[i] && num[j]!=0 && num[i]!= 0)
                    reverse++;
                if(target.getNum()[j]>target.getNum()[i] && target.getNum()[j]!=0 && target.getNum()[i]!=0)
                    reverse++;
            }
        }
        return reverse % 2 == 0;
    }
    /**
     * @return 返回0在八个数码中的位置
     */
    @Contract(pure = true)
    private int getZeroPosition(){
        int position = -1;
        for(int i=0;i<9;i++){
            if(this.num[i] == 0){
                position = i;
            }
        }
        return position;
    }
    /**
     * 去重，当前状态不重复返回-1
     * @param open    状态集合
     * @return 判断当前状态是否存在于open表中
     */
    private int isContains(@org.jetbrains.annotations.NotNull List<EightNumberHelper> open){
        for(int i=0; i<open.size(); i++){
            if(Arrays.equals(open.get(i).getNum(), getNum())){
                return i;
            }
        }
        return -1;
    }

    /**
     * 一维数组
     * @return 小于3（第一行）的不能上移返回false
     */
    @Contract(pure = true)
    private boolean isMoveUp() {
        int position = getZeroPosition();
        return position>2;
    }
    /**
     *
     * @return 大于6（第三行）返回false
     */
    @Contract(pure = true)
    private boolean isMoveDown() {
        int position = getZeroPosition();
        return position<6;
    }
    /**
     *
     * @return 0，3，6（第一列）返回false
     */
    @Contract(pure = true)
    private boolean isMoveLeft() {
        int position = getZeroPosition();
        return position%3!=0;
    }
    /**
     *
     * @return 2，5，8（第三列）不能右移返回false
     */
    @Contract(pure = true)
    private boolean isMoveRight() {
        int position = getZeroPosition();
        return position%3 != 2;
    }
    /**
     *
     * @param move 0：上，1：下，2：左，3：右
     * @return 返回移动后的状态
     */
    public EightNumberHelper moveUp(Action move){
        EightNumberHelper temp = new EightNumberHelper();
        int[] tempNum = num.clone();
        temp.setNum(tempNum);
        int position = getZeroPosition();    //初始的位置
        int p=switch (move) {
            case UP -> position - 3;
            case DOWN -> position + 3;
            case LEFT -> position - 1;
            case RIGHT -> position + 1;
        };//与0换位置的位置
        temp.getNum()[position] = num[p];
        temp.getNum()[p] = 0;
        return temp;
    }
    /**
     * 按照3*3的格式输出
     */
    public void print(){
        for(int i=0;i<9;i++){
            if(i%3 == 2){
                print(String.valueOf(this.num[i]));
            }else{
                System.out.print(this.num[i]+"  ");
            }
        }
    }
    /**
     * 将最终答案路径保存下来并输出
     */
    public void printRoute(){
        EightNumberHelper temp;
        int count = 0;
        temp = this;
        print("----------开始移动----------");
        while(temp!=null){
            answer.add(temp);
            temp = temp.getParent();
            count++;
        }
        for(int i=answer.size()-1 ; i>=0 ; i--){
            answer.get(i).print();
            print("--------------------");
        }
        print("最小移动步数："+(count-1));
    }

    private static void print(String s){
        System.out.println(s);
    }
    /**
     *
     * @param open open表
     * @param close close表
     * @param parent 父状态
     * @param target 目标状态
     */
    public void operation(List<EightNumberHelper> open, List<EightNumberHelper> close, EightNumberHelper parent, EightNumberHelper target){
        if(this.isContains(close) != -1){//如果不在close表中
            return;
        }
        int position = this.isContains(open);//获取在open表中的位置
        if(position == -1){//如果也不在open表中
            this.parent = parent;//指明它的父状态
            this.init(target);//计算它的估计值
            open.add(this);//把它添加进open表
        }else if(this.getDepth() < open.get(position).getDepth()){//如果它在open表中,跟已存在的状态作比较，如果它的步数较少则是较优解
            open.remove(position);//把已经存在的相同状态替换掉
            this.parent = parent;
            this.init(target);
            open.add(this);
        }
    }

    //8 6 7 2 5 4 3 0 1
    //6 4 7 8 5 0 3 2 1
    public static void cal(int[] nowPos,int[] finPos){
        List<EightNumberHelper> open = new ArrayList<>();
        List<EightNumberHelper> close = new ArrayList<>();
        var start = new EightNumberHelper();
        var target = new EightNumberHelper();
        start.setNum(nowPos);
        target.setNum(finPos);
        var startTime = System.currentTimeMillis();
        if(!start.isSolvable(target)){
            print("目标状态不可达");
            return;
        }
        //初始化初始状态
        //包括估值函数的初始化
        start.init(target);
        //添加初始状态
        open.add(start);
        while (!open.isEmpty()){
            //进行从小到大的排序
            open.sort(Comparator.comparingInt(cur -> cur.evaluation));
            final EightNumberHelper best = open.get(0);
            //从open表内部删除匹配值最低的那个
            //并添加到close表里面
            open.remove(0);
            close.add(best);
            if(best.isTarget(target)){
                //输出
                best.printRoute();
                long end=System.currentTimeMillis(); //获取结束时间
                print("程序运行时间： "+(end-startTime)+"ms");
                return;
            }
            Action move;
            //由best状态进行扩展并加入到open表中
            //0的位置上移之后状态不在close和open中设定best为其父状态，并初始化f(n)估值函数
            //可以上移的话
            if(best.isMoveUp()){
                //上移的标记
                move=Action.UP;
                //目标的子状态
                final var up = best.moveUp(move);
                up.operation(open,close,best,target);
            }
            if(best.isMoveDown()) {
                move = Action.DOWN;
                final var down = best.moveUp(move);
                down.operation(open,close,best,target);
            }
            if(best.isMoveRight()){
                move=Action.RIGHT;
                final var right = best.moveUp(move);
                right.operation(open,close,best,target);
            }
            if(best.isMoveLeft()){
                move=Action.LEFT;
                final var left = best.moveUp(move);
                left.operation(open,close,best,target);
            }
        }
    }
}