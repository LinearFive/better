package controller;


import model.ChessColor;
import model.ChessComponent;
import view.ChessGameFrame;
import view.Chessboard;

import javax.swing.*;

import static model.BlackPawnChessComponent.*;
import static model.WhitePawnChessComponent.*;

public class ClickController {
    private final Chessboard chessboard;
    private ChessComponent first;//first反馈的似乎是点击点距离窗口左上角的位置（以像素作为坐标）
    private static int stepCounter = 0;

    public ClickController(Chessboard chessboard) {
        this.chessboard = chessboard;
    }

    public void onClick(ChessComponent chessComponent) {
        if (first == null) {
            if (handleFirst(chessComponent)) {
                chessComponent.setSelected(true);
                first = chessComponent;
                first.repaint();
            }
        } else {
            if (first == chessComponent) { // 再次点击取消选取
                chessComponent.setSelected(false);
                ChessComponent recordFirst = first;
                first = null;
                recordFirst.repaint();
            } else if (handleSecond(chessComponent)) {//执行该语句就说明是正确的点击
                stepCounter++;//记录行棋次数
                //如果进行了吃过路兵，移除过路的兵 / 本方走棋完成后，立即熄灭对方的flag
                setFlagAndPointer(chessComponent.getChessColor());

                //repaint in swap chess method.//swapComponents的作用是移动棋子到目标点，对于底线升变的情形，兵到达底线后需要进行选择
                chessboard.swapChessComponents(first, chessComponent);//此时棋子移动，尚未改变行棋方

                //检验升变的可行性、进行升变的选择
                if (whiteReachBoundary | blackReachBoundary){levelUpPerformer();}

                //交换行棋方 / 改变游戏回合和行棋方显示(注意！更新后的显示器指示的是下一局的回合与行棋方)
                chessboard.swapColor();
                setDisplay(stepCounter);

                first.setSelected(false);
                first = null;

            }
        }
    }

    /**
     * @param chessComponent 目标选取的棋子
     * @return 目标选取的棋子是否与棋盘记录的当前行棋方颜色相同
     */
    private boolean handleFirst(ChessComponent chessComponent) {
        return chessComponent.getChessColor() == chessboard.getCurrentColor();
    }

    /**
     * @param chessComponent first棋子目标移动到的棋子second
     * @return first棋子是否能够移动到second棋子位置
     */
    private boolean handleSecond(ChessComponent chessComponent) {
        return chessComponent.getChessColor() != chessboard.getCurrentColor() &&
                first.canMoveTo(chessboard.getChessComponents(), chessComponent.getChessboardPoint());
    }


    /**
     * 重置‘过路兵条件flag‘，重置‘过路兵位置指示器’
     */
    private void setFlagAndPointer(ChessColor color){
        //如果进行了吃过路兵，移除过路的兵
        if (afterPassBlackFlag){
            chessboard.removePassPawn(chessboard.getChessComponents()[4][whitePointer[0]]);
            setAfterPassBlackFlag(false);
        }else if (afterPassWhiteFlag){
            chessboard.removePassPawn(chessboard.getChessComponents()[3][blackPointer[0]]);
            setAfterPassWhiteFlag(false);
        }
        //本方走pawn两格后，flag亮起，对方完成一次走子后，应立即熄灭flag
        if (color.equals(ChessColor.BLACK)){
            setWhiteFlag(false);//黑方走棋后，白方立即熄灭flag
            whitePointer[0] = -1;
            whitePointer[1] = -1;
            whitePointer[2] = -1;
        }else if (color.equals(ChessColor.WHITE)){
            setBlackFlag(false);//白方走棋后，黑方立即熄灭flag
            blackPointer[0] = -1;
            blackPointer[1] = -1;
            blackPointer[2] = -1;
        }
    }

    /**
     * 兵升变的判定、选择、执行
     * 注意！！！first反馈的似乎是鼠标点击点距离窗口左上角的坐标（像素位置），而非点击的棋子对象
     */
    private void levelUpPerformer(){
        if (whiteReachBoundary){
            //此处加入弹窗进行选择
            int choice = JOptionPane.showOptionDialog(null, "Level up for white pawn", "LevelUpPanel",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[]{"queen","bishop","knight","rook"}, "queen");
            choice = choice==-1 ? 0 : choice;
            chessboard.levelUp(chessboard.getChessComponents()[0][whiteUpCol],choice);
            //选择完毕后关闭whiteReachBoundary的Flag，恢复位置指示
            setWhiteReachBoundary(false);
            setWhiteUpCol(-1);
        }else if (blackReachBoundary){
            //此处加入弹窗进行选择
            int choice = JOptionPane.showOptionDialog(null, "Level up for black pawn", "LevelUpPanel",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[]{"queen","bishop","knight","rook"}, "queen");
            choice = choice==-1 ? 0 : choice;
            chessboard.levelUp(chessboard.getChessComponents()[7][blackUpCol],choice);
            //选择完毕后关闭whiteReachBoundary的Flag，恢复位置指示
            setBlackReachBoundary(false);
            setBlackUpCol(-1);
        }
    }

    /**
     * 设置Label显示Round以及当前行棋方
     */
    public static void setDisplay(int stepCounter){
        int roundNumber = stepCounter/2 + 1;
        String colorDisplay = stepCounter%2==0 ? "White" : "Black";
        ChessGameFrame.setRoundAndPlayerDisplay("Round : " + roundNumber + ", Player : " + colorDisplay);
    }

    //更新计步器

    public static int getStepCounter() {
        return stepCounter;
    }
    public static void setStepCounter(int stepCounter) {
        ClickController.stepCounter = stepCounter;
    }
}
