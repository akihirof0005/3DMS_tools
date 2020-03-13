
/* Copyright (C) 2004-2007  Rajarshi Guha <rajarshi@users.sourceforge.net>
 *                    2014  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package glycan;

import java.util.Map;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;
import manipulation.Vector3D;

public class Kabsch {

    private Vector3D[] getVector3DArray(Atom[] a) {
        Vector3D p[] = new Vector3D[a.length];
        for (int i = 0; i < a.length; i++)
            p[i] = a[i].Vector3D;

        return p;
    }

    private double[] getAtomicMasses(Atom a[], Map<String, Double> factory) {
        double am[] = new double[a.length];
        // IsotopeFactory factory = null;
        for (int i = 0; i < a.length; i++)
            am[i] = factory.get(a[i].symbol);
        return am;
    }

    private Vector3D getCenterOfMass(Vector3D p[], double atwt[]) {
        double x = 0.0D;
        double y = 0.0D;
        double z = 0.0D;
        double totalmass = 0.0D;
        for (int i = 0; i < p.length; i++) {

            x += atwt[i] * p[i].crd[0];
            y += atwt[i] * p[i].crd[1];
            z += atwt[i] * p[i].crd[2];
            totalmass += atwt[i];
        }

        return new Vector3D(x / totalmass, y / totalmass, z / totalmass);
    }

    public Kabsch(Atom al1[], Atom al2[], Map<String, Double> factory) {
        rmsd = -1D;
        if (al1.length != al2.length)
            System.out.println("The Atom[]'s being aligned must have the same numebr of atoms");
        npoint = al1.length;
        p1 = getVector3DArray(al1);
        p2 = getVector3DArray(al2);
        wts = new double[npoint];
        atwt1 = getAtomicMasses(al1, factory);
        atwt2 = getAtomicMasses(al2, factory);
        for (int i = 0; i < npoint; i++)
            wts[i] = 1.0D;

    }

    public void align() {
        cm1 = new Vector3D();
        cm2 = new Vector3D();
        cm1 = getCenterOfMass(p1, atwt1);
        cm2 = getCenterOfMass(p2, atwt2);
        for (int i = 0; i < npoint; i++) {
            p1[i].crd[0] = p1[i].crd[0] - cm1.crd[0];
            p1[i].crd[1] = p1[i].crd[1] - cm1.crd[1];
            p1[i].crd[2] = p1[i].crd[2] - cm1.crd[2];
            p2[i].crd[0] = p2[i].crd[0] - cm2.crd[0];
            p2[i].crd[1] = p2[i].crd[1] - cm2.crd[1];
            p2[i].crd[2] = p2[i].crd[2] - cm2.crd[2];
        }

        double tR[][] = new double[3][3];
        for (int i = 0; i < npoint; i++)
            wts[i] = 1.0D;

        for (int i = 0; i < npoint; i++) {
            tR[0][0] += p1[i].crd[0] * p2[i].crd[0] * wts[i];
            tR[0][1] += p1[i].crd[0] * p2[i].crd[1] * wts[i];
            tR[0][2] += p1[i].crd[0] * p2[i].crd[2] * wts[i];
            tR[1][0] += p1[i].crd[1] * p2[i].crd[0] * wts[i];
            tR[1][1] += p1[i].crd[1] * p2[i].crd[1] * wts[i];
            tR[1][2] += p1[i].crd[1] * p2[i].crd[2] * wts[i];
            tR[2][0] += p1[i].crd[2] * p2[i].crd[0] * wts[i];
            tR[2][1] += p1[i].crd[2] * p2[i].crd[1] * wts[i];
            tR[2][2] += p1[i].crd[2] * p2[i].crd[2] * wts[i];
        }

        double R[][] = new double[3][3];
        Matrix tmp = new Matrix(tR);
        R = tmp.transpose().getArray();
        double RtR[][] = new double[3][3];
        Matrix jamaR = new Matrix(R);
        tmp = tmp.times(jamaR);
        RtR = tmp.getArray();
        Matrix jamaRtR = new Matrix(RtR);
        EigenvalueDecomposition ed = jamaRtR.eig();
        double mu[] = ed.getRealEigenvalues();
        double a[][] = ed.getV().getArray();
        double tmp2 = mu[2];
        mu[2] = mu[0];
        mu[0] = tmp2;
        for (int i = 0; i < 3; i++) {
            tmp2 = a[i][2];
            a[i][2] = a[i][0];
            a[i][0] = tmp2;
        }

        a[0][2] = a[1][0] * a[2][1] - a[1][1] * a[2][0];
        a[1][2] = a[0][1] * a[2][0] - a[0][0] * a[2][1];
        a[2][2] = a[0][0] * a[1][1] - a[0][1] * a[1][0];
        double b[][] = new double[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++)
                    b[i][j] += R[i][k] * a[k][j];

                b[i][j] = b[i][j] / Math.sqrt(mu[j]);
            }

        }

        double norm1 = 0.0D;
        double norm2 = 0.0D;
        for (int i = 0; i < 3; i++) {
            norm1 += b[i][0] * b[i][0];
            norm2 += b[i][1] * b[i][1];
        }

        norm1 = Math.sqrt(norm1);
        norm2 = Math.sqrt(norm2);
        for (int i = 0; i < 3; i++) {
            b[i][0] = b[i][0] / norm1;
            b[i][1] = b[i][1] / norm2;
        }

        b[0][2] = b[1][0] * b[2][1] - b[1][1] * b[2][0];
        b[1][2] = b[0][1] * b[2][0] - b[0][0] * b[2][1];
        b[2][2] = b[0][0] * b[1][1] - b[0][1] * b[1][0];
        double tU[][] = new double[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++)
                    tU[i][j] += b[i][k] * a[j][k];

            }

        }

        U = new double[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++)
                U[i][j] = tU[j][i];

        }

        rp = new Vector3D[npoint];
        for (int i = 0; i < npoint; i++)
            rp[i] = new Vector3D(U[0][0] * p2[i].crd[0] + U[0][1] * p2[i].crd[1] + U[0][2] * p2[i].crd[2],
                    U[1][0] * p2[i].crd[0] + U[1][1] * p2[i].crd[1] + U[1][2] * p2[i].crd[2],
                    U[2][0] * p2[i].crd[0] + U[2][1] * p2[i].crd[1] + U[2][2] * p2[i].crd[2]);

        double rms = 0.0D;
        for (int i = 0; i < npoint; i++)
            rms += (p1[i].crd[0] - rp[i].crd[0]) * (p1[i].crd[0] - rp[i].crd[0])
                    + (p1[i].crd[1] - rp[i].crd[1]) * (p1[i].crd[1] - rp[i].crd[1])
                    + (p1[i].crd[2] - rp[i].crd[2]) * (p1[i].crd[2] - rp[i].crd[2]);

        rmsd = Math.sqrt(rms / (double) npoint);
    }

    public double getRMSD() {
        return rmsd;
    }

    public double[][] getRotationMatrix() {
        return U;
    }

    public Vector3D getCenterOfMass() {
        return cm1;
    }

    public void rotateAtomContainer(Atom[] ac) {
        Vector3D p[] = getVector3DArray(ac);
        for (int i = 0; i < ac.length; i++) {
            p[i].crd[0] = p[i].crd[0] - cm2.crd[0];
            p[i].crd[1] = p[i].crd[1] - cm2.crd[1];
            p[i].crd[2] = p[i].crd[2] - cm2.crd[2];
            ac[i].Vector3D = new Vector3D(U[0][0] * p[i].crd[0] + U[0][1] * p[i].crd[1] + U[0][2] * p[i].crd[2],
                    U[1][0] * p[i].crd[0] + U[1][1] * p[i].crd[1] + U[1][2] * p[i].crd[2],
                    U[2][0] * p[i].crd[0] + U[2][1] * p[i].crd[1] + U[2][2] * p[i].crd[2]);
        }

    }

    private double U[][];
    private double rmsd;
    private Vector3D p1[];
    private Vector3D p2[];
    private Vector3D rp[];
    private double wts[];
    private int npoint;
    private Vector3D cm1;
    private Vector3D cm2;
    private double atwt1[];
    private double atwt2[];

}
