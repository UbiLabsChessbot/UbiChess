package me.michaeljiang.movesystemlibs.movesystem.component.arm7bot.util;


import me.michaeljiang.movesystemlibs.movesystem.component.arm7bot.model.ArmParameter;
import me.michaeljiang.movesystemlibs.movesystem.component.arm7bot.model.PVector;

/**
 * Created by MichaelJiang on 2017/4/22.
 * 机械手发送数据前Check是否能移动到指定位置
 */

public class ArmCheck {
    private static final String TAG = ArmCheck.class.getSimpleName();

    /*****ArmCheck初始化参数参数，如需修改请在ArmParameter中修改*****/
    private double[] theta = new double[ArmParameter.SERVO_NUM];//保存各舵机移动所需要的角度
    private double[] thetaMin = ArmParameter.thetaMin;
    private double[] thetaMax = ArmParameter.thetaMax;
    private double a=ArmParameter.a, b=ArmParameter.b, c=ArmParameter.c, d=ArmParameter.d;
    private double e=ArmParameter.e, f=ArmParameter.f, g=ArmParameter.g, h =ArmParameter.h;
    private PVector[] joint = new PVector[9];
    private PVector j6=new PVector();
    private PVector vec56 = new PVector();
    private PVector vec67 = new PVector();

    /**
     * ArmCheck需要初始化初始化一次后无需进行重复初始化
     */
    public ArmCheck(){
        for(int i=0;i<joint.length;i++){
            joint[i]=new PVector();
        }
        for(int i=0;i<theta.length;i++){
            theta[i]=0;
        }
    }

    public boolean checkMotoData(int[] motoData){
        for(int i = 0; i < motoData.length; i++){
            if(motoData[i]>1000||motoData[i]<0){
                return false;
            }
        }
        return true;
    }


    /**
     * @param IK6 符合Arm7Bot通讯指令的一串byte数组
     * 样例：  private byte[] IK6={(byte)0xfe,(byte)0xFA,0x08,0x00,0x01,0x2F,0x00,0x64,0x08,0x00,0x08,0x00, 0x09,0x44,0x01,0x48,0x01,0x48,0x01,0x48,0x01,0x48};
     * @return
     * 如果符合要求则返回true
     * 如果不符合要求则返回false
     */

    public boolean checkIK6Byte(byte[] IK6){
        int[] data=new int[10];
        int j=0;
        double theta6;
        for (int i = 1; i < 11; i++) {
            data[i-1] = IK6[i * 2] * 128 + IK6[i * 2 + 1];
        }
        //
        int mul;
        mul = 1; if (data[0] > 1024) mul = -1; j6.x = data[0] % 1024 * mul;
        mul = 1; if (data[1] > 1024) mul = -1; j6.y = data[1] % 1024 * mul;
        mul = 1; if (data[2] > 1024) mul = -1; j6.z = data[2] % 1024 * mul;
        //
        mul = 1; if (data[3] > 1024) mul = -1; vec56.x = data[3] % 1024 * mul;
        mul = 1; if (data[4] > 1024) mul = -1; vec56.y = data[4] % 1024 * mul;
        mul = 1; if (data[5] > 1024) mul = -1; vec56.z = data[5] % 1024 * mul;
        //
        mul = 1; if (data[6] > 1024) mul = -1; vec67.x = data[6] % 1024 * mul;
        mul = 1; if (data[7] > 1024) mul = -1; vec67.y = data[7] % 1024 * mul;
        mul = 1; if (data[8] > 1024) mul = -1; vec67.z = data[8] % 1024 * mul;

        theta6 = ((double)(data[9])) * 9 / 50;

        if(checkIK6(j6,vec56,vec67)==0)
            return true;
        else
            return false;
    }

    public int checkIK6(PVector j6, PVector vec56_d, PVector vec67_d){
        int status = -1;
        int IK5_status = checkIK5(j6, vec56_d);
        if (IK5_status != 0) return IK5_status;
        PVector vec67_u = new PVector(vec67_d.x, vec67_d.y, vec67_d.z);
        vec67_u.normalize();
        PVector j7 = new PVector(j6.x + g * vec67_u.x, j6.y + g * vec67_u.y, j6.z + g * vec67_u.z);
        PVector j7p = calcProjectionPt(j7, j6, vec56_d);
        theta[5] = 0; calcJoints();
        PVector j7_0 = joint[7];
        PVector vec67_0 = new PVector(j7_0.x - j6.x, j7_0.y - j6.y, j7_0.z - j6.z);
        PVector vec67p = new PVector(j7p.x - j6.x, j7p.y - j6.y, j7p.z - j6.z);
        //(3)- calculate theta[5]
        double thetaTmp5 =  Math.acos( vec67_0.dot(vec67p) / (j6.dist(j7_0) * j6.dist(j7p)) );
        theta[5] = -thetaTmp5;
        if (vec67_d.x < 0) theta[5] = -theta[5];
        if (theta[5] < 0) theta[5] = Math.PI + theta[5];
        calcJoints();
        return 0;
    }//检查IK6发送是否合理

    public int checkIK5(PVector j6, PVector vec56_d) {
        int status = -1;
        PVector vec56_u = new PVector(vec56_d.x, vec56_d.y, vec56_d.z);
        vec56_u.normalize();
        PVector j5 = new PVector(j6.x - f * vec56_u.x, j6.y - f * vec56_u.y, j6.z - f * vec56_u.z);
        PVector vec56 = new PVector(j6.x - j5.x, j6.y - j5.y, j6.z - j5.z);
        int IK3_status = checkIk3(j5);
        //println("IK3_status: ", IK3_status);
        if (IK3_status != 0) return IK3_status;
        joint[5] = j5;
        theta[3] = 0.;
        theta[4] = 0.;
        calcJoints();
        PVector j6_0 = joint[6];
        PVector vec56_0 = new PVector(j6_0.x - j5.x, j6_0.y - j5.y, j6_0.z - j5.z);
        PVector vec45 = new PVector(joint[5].x - joint[4].x, joint[5].y - joint[4].y, joint[5].z - joint[4].z);
        PVector j6p = calcProjectionPt(j6, j5, vec45);
        PVector vec56p = new PVector(j6p.x - j5.x, j6p.y - j5.y, j6p.z - j5.z);
        //Serial.print("vec56p= "); Serial.print( vec56p.x ); Serial.print(" ");Serial.print( vec56p.y ); Serial.print(" ");Serial.println( vec56p.z );
        theta[3] = Math.acos(vec56_0.dot(vec56p) / (j5.dist(j6_0) * j5.dist(j6p)));
        theta[4] = Math.acos(vec56.dot(vec56p) / (j5.dist(j6) * j5.dist(j6p)));
        calcJoints();
        double dist = j6.dist(joint[6]);
        if (dist < 1) {
            return 0;
        }
        theta[3] = Math.PI - theta[3];
        theta[4] = Math.PI - theta[4];
        calcJoints();
        dist = j6.dist(joint[6]);
        if (dist < 1) {
            return 0;
        } else {
            return 2;
        }
    }//检查IK5发送是否合理

    public int checkIk3(PVector pt){
        double x = pt.x, y = pt.y, z = pt.z;
        int status = 1;
        theta[0] = Math.atan(y / x);
        if (theta[0] < 0) theta[0] = Math.PI + theta[0];
        x -= d * Math.cos(theta[0]);
        y -= d * Math.sin(theta[0]);
        z -= e;
        double lengthA = Math.sqrt(x * x + y * y + z * z);
        double lengthC = Math.sqrt(h * h + c * c);
        double offsetAngle = Math.atan(h / c);
        double angleA = Math.acos( (a * a + lengthC * lengthC - lengthA * lengthA) / (2 * a * lengthC) );
        double angleB = Math.atan( z / Math.sqrt(x * x + y * y) );
        double angleC = Math.acos( (a * a + lengthA * lengthA - lengthC * lengthC) / (2 * a * lengthA) );
        theta[1] = angleB + angleC;
        theta[2] = Math.PI - angleA - angleB - angleC + offsetAngle;
        theta[2] += 1.134464;

        // range check
        if (theta[1] > thetaMin[1] && theta[1] < thetaMax[1] &&
                theta[2] > thetaMin[2] && theta[2] < thetaMax[2] &&
                theta[2] - 0.8203047 + theta[1] < Math.PI && theta[2] + theta[1] > 1.44862327) {
            status = 0;
        }
        return status;
    }//检查IK3发送是否合理

    private PVector calcProjectionPt(PVector pt0, PVector pt1, PVector nVec) {
        PVector n = new PVector(nVec.x, nVec.y, nVec.z);
        n.normalize();
        PVector vec10 =new PVector(pt0.x - pt1.x, pt0.y - pt1.y, pt0.z - pt1.z);
        double dot = vec10.dot(n);
        PVector projectionPt =new PVector(pt0.x - dot * n.x, pt0.y - dot * n.y, pt0.z - dot * n.z);
        return projectionPt;
    }

    private void calcJoints(){
        joint[1] = new PVector(joint[0].x, joint[0].y + d, joint[0].z + e);
        joint[2] = new PVector(0, -b * Math.cos(theta[2] - 1.134464), b * Math.sin(theta[2] - 1.134464));    joint[2].add(joint[1]);
        joint[3] = new PVector(0, a * Math.cos(theta[1]), a * Math.sin(theta[1]));     joint[3].add(joint[1]);
        joint[4] = new PVector(0, h * Math.sin(theta[2] - 1.134464), h * Math.cos(theta[2] - 1.134464));     joint[4].add(joint[3]);
        joint[5] = new PVector(0, c * Math.cos(theta[2] - 1.134464), -c * Math.sin(theta[2] - 1.134464));    joint[5].add(joint[4]);
        joint[6] = new PVector(0, f * Math.sin(theta[2] - 1.134464 + theta[4]), f * Math.cos(theta[2] - 1.134464 + theta[4]));    joint[6].add(joint[5]);
        joint[7] = new PVector(0, -g * Math.cos(theta[2] - 1.134464 + theta[4]), g * Math.sin(theta[2] - 1.134464 + theta[4]));   joint[7].add(joint[6]);
        joint[7] = arbitraryRotate(joint[7], joint[6], joint[5], theta[5]);
        joint[6] = arbitraryRotate(joint[6], joint[5], joint[4], theta[3] - Math.PI/2.0);
        joint[7] = arbitraryRotate(joint[7], joint[5], joint[4], theta[3] - Math.PI/2.0);
        joint[8] = new PVector(2 * joint[6].x - joint[7].x, 2 * joint[6].y - joint[7].y, 2 * joint[6].z - joint[7].z);
        for (int i = 1; i < 9; i++) {
            joint[i] = zAxiRotate(joint[i], theta[0] - Math.PI/2.0);
        }
    }

    private PVector arbitraryRotate(PVector point, PVector pointA, PVector pointB, double _angle){
        PVector pt = new PVector(0, 0, 0);
        double x = point.x, y = point.y, z = point.z;
        double u = pointB.x - pointA.x, v = pointB.y - pointA.y, w = pointB.z - pointA.z;
        double l = Math.sqrt(u * u + v * v + w * w);
        u /= l; v /= l; w /= l;
        double a = pointA.x, b = pointA.y, c = pointA.z;
        double u2 = u * u, v2 = v * v, w2 = w * w;
        double au = a * u, av = a * v, aw = a * w;
        double bu = b * u, bv = b * v, bw = b * w;
        double cu = c * u, cv = c * v, cw = c * w;
        double ux = u * x, uy = u * y, uz = u * z;
        double vx = v * x, vy = v * y, vz = v * z;
        double wx = w * x, wy = w * y, wz = w * z;
        pt.x = (a * (v2 + w2) - u * (bv + cw - ux - vy - wz)) * (1 - Math.cos(_angle)) + x * Math.cos(_angle) + (-cv + bw - wy + vz) * Math.sin(_angle);
        pt.y = (b * (u2 + w2) - v * (au + cw - ux - vy - wz)) * (1 - Math.cos(_angle)) + y * Math.cos(_angle) + (cu - aw + wx - uz) * Math.sin(_angle);
        pt.z = (c * (u2 + v2) - w * (au + bv - ux - vy - wz)) * (1 - Math.cos(_angle)) + z * Math.cos(_angle) + (-bu + av - vx + uy) * Math.sin(_angle);
        return pt;
    }

    private PVector zAxiRotate(PVector point, double _angle) {
        PVector pt;
        pt = new PVector( Math.cos(_angle) * point.x - Math.sin(_angle) * point.y, Math.sin(_angle) * point.x + Math.cos(_angle) * point.y, point.z );
        return pt;
    }


}
