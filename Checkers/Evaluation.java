/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Checkers;

import java.util.LinkedList;

/**
 *
 * @author iandardik
 */
public class Evaluation {
    private LinkedList<Param> learning = new LinkedList<Param>();
    private LinkedList<Param> lrnReserve = new LinkedList<Param>();
    private LinkedList<Param> critic = new LinkedList<Param>();
    private LinkedList<Param> crReserve = new LinkedList<Param>();

    public Evaluation() {
        critic.add(new Param(0, 20));
        critic.add(new Param(1, 4));
        critic.add(new Param(2, 3));
        critic.add(new Param(3, 50));
        critic.add(new Param(4, 1));
        critic.add(new Param(5, 1));

//        crReserve.add(new Param(4, 1));
//        crReserve.add(new Param(5, 1));

        learning.add(new Param(0, 20));
        learning.add(new Param(1, 4));
        learning.add(new Param(2, 3));
        learning.add(new Param(3, 50));

        lrnReserve.add(new Param(4, 1));
        lrnReserve.add(new Param(5, 1));
    }

    public Evaluation(boolean f) {}

    public double eval(Board b) {
        double estimate = 0;
        for (Param p : learning)
            estimate += p.eval(b);
        return estimate;
    }

    public static Evaluation evalAndLearn(Board ahead, Board cur, Evaluation e) {
        // make sure we only evaluate on our turn

        LinkedList<Param> nLearn = new LinkedList<Param>();
        LinkedList<Param> nReserve = new LinkedList<Param>();
        for (Param p : e.learning)
            nLearn.add(p.copy());
        for (Param p : e.lrnReserve)
            nReserve.add(p.copy());
        LinkedList<Param> nCritic = new LinkedList<Param>();
        LinkedList<Param> nCrReserve = new LinkedList<Param>();
        for (Param p : e.critic)
            nCritic.add(p.copy());
        for (Param p : e.crReserve)
            nCrReserve.add(p.copy());

        double estimate = 0;
        double actual = 0;
        for (Param p : e.learning) {
            estimate += p.eval(cur);
//            System.out.println("original param"+p.paramNo+": "+p.coefficient);
        }
        for (Param p : e.critic)
            actual += p.eval(ahead);

        /////// balancing //////
        estimate = estimate * e.critic.size() / e.learning.size(); // cross multiply
        actual = actual * e.learning.size() / e.critic.size();
        ////////////////////////

        double diff = actual - estimate;
//        double delta = diff / actual;
        if (diff > 0) {
            double c = Math.abs(2 -  (estimate/actual));
//            System.out.println("hi c: "+c);
            for (Param p : nLearn) {
                p.coefficient += 0.1 *c;
//                System.out.println("learned param"+p.paramNo+": "+p.coefficient);
            }
        } else if (diff < 0) {
            double c = Math.abs(actual/estimate);
//            System.out.println("lo c: "+c);
            for (Param p : nLearn) {
                p.coefficient -= 0.1*c;
//                System.out.println("learned param"+p.paramNo+": "+p.coefficient);
            }
        }
//        System.out.println();

        Evaluation nEval = new Evaluation(false);
        nEval.learning = nLearn;
        nEval.lrnReserve = nReserve;
        nEval.critic = nCritic;
        nEval.crReserve = nCrReserve;


//        System.out.println("F: "+nLearn.toString());

        e.randomizedRotateCoefficients();
//        e.rotateCrCoefficients();
        nEval.randomizedRotateCoefficients();
//        nEval.rotateCrCoefficients();

//        System.out.println("go! "+e.learning.toString());

        return nEval;
    }

    private void rotateCoefficients() {
        Param rmv = learning.removeLast();
        Param add = lrnReserve.removeFirst();
        learning.addFirst(add);
        lrnReserve.addLast(rmv);
    }

    private void randomizedRotateCoefficients() {
        double r = Math.random();

        if (r < 0.25) {
            Param rmv = learning.removeFirst();
            Param add = lrnReserve.removeFirst();
            if (r < .25/2) {
                learning.addFirst(add);
                lrnReserve.addFirst(rmv);
            } else {
                learning.addLast(add);
                lrnReserve.addLast(rmv);
            }
        } else if (r < 0.5) {
            Param rmv = learning.removeFirst();
            Param add = lrnReserve.removeLast();
            if (r < 0.25 + 0.25/2) {
                learning.addLast(add);
                lrnReserve.addFirst(rmv);
            } else {
                learning.addFirst(add);
                lrnReserve.addLast(rmv);
            }
        } else if (r < 0.75) {
            Param rmv = learning.removeLast();
            Param add = lrnReserve.removeFirst();
            if (r < 0.5 + 0.25/2) {
                learning.addFirst(add);
                lrnReserve.addLast(rmv);
            } else {
                learning.addLast(add);
                lrnReserve.addFirst(rmv);
            }
        } else {
            Param rmv = learning.removeLast();
            Param add = lrnReserve.removeLast();
            if (r < 0.75 + 0.25/2) {
                learning.addLast(add);
                lrnReserve.addLast(rmv);
            } else {
                learning.addFirst(add);
                lrnReserve.addFirst(rmv);
            }
        }
    }

    private void rotateCrCoefficients() {
        Param rmv = critic.removeLast();
        Param add = crReserve.removeFirst();
        critic.addFirst(add);
        crReserve.addLast(rmv);
    }

    @Override
    public String toString() {
        return "F in use: \n\t"+learning+"\nF in reserve: \n\t"+lrnReserve;
    }



    private class Param {
        public final int paramNo;
        public double coefficient;

        public Param(int no, double c) {
            paramNo = no;
            coefficient = c;
        }

        public double eval(Board b) {
            switch (paramNo) {
                case 0:
                    return coefficient * b.pieceAdvantage(b.turn());
                case 1:
                    return coefficient * b.noBackRow(b.turn());
                case 2:
                    return coefficient * b.pawnAdvancement(b.turn());
                case 3:
                    return coefficient * b.pieceRatio(b.turn());
                case 4:
                    return coefficient * b.kingAttack(b.turn());
                case 5:
                    return coefficient * b.pullThrough(b.turn());
            }

            throw new RuntimeException("no param");
        }

        public Param copy() {
            return new Param(paramNo, coefficient);
        }

        @Override
        public String toString() {
            return "Param no: "+paramNo+", coef: "+coefficient;
        }
    }
}