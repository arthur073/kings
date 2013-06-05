/*
    The file is copyright Andrew Wilkinson <aw@cs.york.ac.uk> 2004.

    This file is part of PyLinda (http://www-users.cs.york.ac.uk/~aw/pylinda)

    PyLinda is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    PyLinda is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Foobar; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

#include <stdio.h>
#include <linda.h>

#define MaxIters 256*256

int main(int argc, char* argv[]) {
    double col_mul;
    col_mul = (pow(2, 24)-1)/((double)MaxIters);
    Tuple template;
    Tuple t;
    Linda_tuplespace out;
    Linda_tuplespace pix;

    Linda_connect(Linda_port);

    template = Tuple_new(2);
    Tuple_set(template, 0, Value_string("out"));
    Tuple_set(template, 1, Value_tuplespaceType);
    t = Linda_rd(Linda_uts, template);
    Tuple_free(template);

    out = (Linda_tuplespace)malloc(strlen(Value_get_tsref(Tuple_get(t, 1))));
    strcpy(out, Value_get_tsref(Tuple_get(t, 1)));
    Tuple_free(t);

    template = Tuple_new(2);
    Tuple_set(template, 0, Value_string("pix"));
    Tuple_set(template, 1, Value_tuplespaceType);
    t = Linda_rd(Linda_uts, template);
    Tuple_free(template);

    pix = (Linda_tuplespace)malloc(strlen(Value_get_tsref(Tuple_get(t, 1))));
    strcpy(pix, Value_get_tsref(Tuple_get(t, 1)));

    template = Tuple_new(4);
    Tuple_set(template, 0, Value_intType);
    Tuple_set(template, 1, Value_intType);
    Tuple_set(template, 2, Value_floatType);
    Tuple_set(template, 3, Value_floatType);

    int i = 0;
    while(1) {
        int x; int y;
        double cr; double ci;
        double zr; double zi;
        double rsquared; double isquared;
        int count;

        t = Linda_inp(out, template);
        if(t == NULL) {
            break;
        }

        x = Value_get_int(Tuple_get(t, 0));
        y = Value_get_int(Tuple_get(t, 1));
        printf("%i\r", i);
        i++;

        cr = Value_get_float(Tuple_get(t, 2));
        ci = Value_get_float(Tuple_get(t, 3));

        Tuple_free(t);

        zr = 0.0; zi = 0.0;
        rsquared = zr * zr;
        isquared = zi * zi;
        count = -1;
        while(rsquared + isquared <= 4.0 && count < MaxIters) {
            count += 1;

            zi = zr * zi * 2 + ci;
            zr = rsquared - isquared + cr;

            rsquared = zr * zr;
            isquared = zi * zi;
        }

        count = (int)(count * col_mul);

        t = Tuple_new(5);
        Tuple_set(t, 0, Value_int(x));
        Tuple_set(t, 1, Value_int(y));
        Tuple_set(t, 2, Value_int((count)%256));
        Tuple_set(t, 3, Value_int((count>>8)%256));
        Tuple_set(t, 4, Value_int((count>>16)%256));

        Linda_out(pix, t);
        Tuple_free(t);
    }

    Tuple_free(template);

    Linda_deleteReference(out); free(out);
    Linda_deleteReference(pix); free(pix);

    Linda_disconnect();

    return 0;
}
