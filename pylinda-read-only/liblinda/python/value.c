/*
*    Copyright 2006 Andrew Wilkinson <aw@cs.york.ac.uk>.
*
*    This file is part of LibLinda (http://www-users.cs.york.ac.uk/~aw/pylinda)
*
*    LibLinda is free software; you can redistribute it and/or modify
*    it under the terms of the GNU Lesser General Public License as published by
*    the Free Software Foundation; either version 2.1 of the License, or
*    (at your option) any later version.
*
*    LibLinda is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU Lesser General Public License for more details.
*
*    You should have received a copy of the GNU Lesser General Public License
*    along with LibLinda; if not, write to the Free Software
*    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

#include "config.h"

#include <stdio.h>

#include PYTHON_H

#if (PY_VERSION_HEX < 0x02050000)
typedef int Py_ssize_t;
#define lenfunc inquiry
#define ssizeargfunc intargfunc
#define ssizeobjargproc intobjargproc
#endif

#include "linda_python.h"
#define HACKY_MAGIC
#include "../../minimal/src/minimal_internal.h"

static PyObject* linda_Value_new(PyTypeObject* type, PyObject* args, PyObject* kwds)
{
    linda_ValueObject* self;

    self = (linda_ValueObject*)type->tp_alloc(type, 0);
    if (self != NULL) {
        self->val = NULL;
    }

    return (PyObject*)self;
}

static int linda_Value_init(linda_ValueObject* self, PyObject* args, PyObject* kwds) {
    int i;
    PyObject* obj = NULL;
#ifdef TYPES
    PyObject* type = NULL;

    if(!PyArg_ParseTuple(args, "|OO", &obj, &type))
        return -1;
#else
    if(!PyArg_ParseTuple(args, "|O", &obj))
        return -1;
#endif

    if(PyObject_IsInstance(obj, (PyObject*)&linda_ValueType)) {
        Linda_addReference(((linda_ValueObject*)obj)->val);
        self->val = ((linda_ValueObject*)obj)->val;
    } else if(obj == Py_None) {
        self->val = Linda_nil();
    } else if(PyInt_Check(obj)) {
        self->val = Linda_long(PyInt_AsLong(obj));
    } else if(PyFloat_Check(obj)) {
        self->val = Linda_float(PyFloat_AsDouble(obj));
    } else if(PyString_Check(obj)) {
        self->val = Linda_string(PyString_AsString(obj));
    } else if(PyTuple_Check(obj)) {
        self->val = Linda_tuple(PyTuple_Size(obj));
        for(i = 0; i < PyTuple_Size(obj); i++) {
            PyObject* o = PyTuple_GetItem(obj, i);
            LindaValue nv = PyO2Value(o);
            if(nv == NULL) {
                Linda_delReference(self->val);
                PyErr_SetString(PyExc_TypeError, "linda_Value_init: Failed to convert value when converting tuple.\n");
                return -1;
            }
            Linda_tupleSet(self->val, i, nv); /* Steals reference to nv. */
        }
    } else if(PyType_Check(obj)) {
        if(obj == (PyObject*)&PyInt_Type) {
            Linda_addReference(Linda_longType);
            self->val = Linda_longType;
        } else if(obj == (PyObject*)&PyFloat_Type) {
            Linda_addReference(Linda_floatType);
            self->val = Linda_floatType;
        } else if(obj == (PyObject*)&PyString_Type) {
            Linda_addReference(Linda_stringType);
            self->val = Linda_stringType;
        } else if(obj == (PyObject*)&PyBool_Type) {
            Linda_addReference(Linda_boolType);
            self->val = Linda_boolType;
        } else if(obj == (PyObject*)&linda_TupleSpaceType) {
            Linda_addReference(Linda_tupleSpaceType);
            self->val = Linda_tupleSpaceType;
        } else if(obj == (PyObject*)&linda_ValueType) {
            Linda_addReference(((linda_ValueObject*)obj)->val);
            self->val = ((linda_ValueObject*)obj)->val;
        } else {
            PyErr_SetString(PyExc_TypeError, "linda_Value_init: Invalid type of type.\n");
            return -1;
        }
    } else if(Linda_is_server && PyObject_IsInstance(obj, (PyObject*)&linda_TSRefType)) {
        Linda_addReference(((linda_TSRefObject*)obj)->ts);
        self->val = ((linda_TSRefObject*)obj)->ts;
    } else if(!Linda_is_server && PyObject_TypeCheck(obj, &linda_TupleSpaceType)) {
        Linda_addReference(((linda_TupleSpaceObject*)obj)->ts);
        self->val = ((linda_TupleSpaceObject*)obj)->ts;
    } else {
        PyErr_Format(PyExc_TypeError, "linda_Value_init: Invalid type. %s.\n", PyString_AsString(PyObject_Str(obj)));
        return -1;
    }

#ifdef TYPES
    if(type != NULL && PyObject_IsInstance(type, (PyObject*)&linda_ValueType)) {
        Linda_setType(self->val, ((linda_ValueObject*)type)->val);
    } else if(type != NULL) {
        PyErr_SetString(PyExc_TypeError, "PyO2Value: Invalid type object.\n");
        return -1;
    }
#endif

    return 0;
}

static void linda_Value_dealloc(linda_ValueObject* self) {
    if(self->val != NULL) {
        Linda_delReference(self->val);
    }
    self->ob_type->tp_free(self);
}

static int linda_Value_cmp(linda_ValueObject* self, linda_ValueObject* other) {
    if(PyErr_Occurred()) {
        fprintf(stderr, "Warning, exception set when entering linda_Value_cmp. Clearing.\n");
        PyErr_Print();
    }
    if(self->val->type != other->val->type) {
        if(self->val->type < other->val->type) {
            return -1;
        } else {
            return 1;
        }
    }
    switch(self->val->type) {
    case M_NIL:
        return 0;
    case M_BOOLEAN:
        if(self->val->boolean == 0 && other->val->boolean == 0) {
            return 0;
        } else if(self->val->boolean == 0 && other->val->boolean == 1) {
            return -1;
        } else if(self->val->boolean == 1 && other->val->boolean == 0) {
            return 1;
        } else {
            return 0;
        }
    case M_INTEGER:
        if(self->val->integer < other->val->integer) {
            return -1;
        } else if(self->val->integer > other->val->integer) {
            return 1;
        } else {
            return 0;
        }
    case M_UINTEGER:
        if(self->val->uinteger < other->val->uinteger) {
            return -1;
        } else if(self->val->uinteger > other->val->uinteger) {
            return 1;
        } else {
            return 0;
        }
    case M_FLOAT:
        if(self->val->singlefloat < other->val->singlefloat) {
            return -1;
        } else if(self->val->singlefloat > other->val->singlefloat) {
            return 1;
        } else {
            return 0;
        }
    case M_DOUBLE:
        if(self->val->doublefloat < other->val->doublefloat) {
            return -1;
        } else if(self->val->doublefloat > other->val->doublefloat) {
            return 1;
        } else {
            return 0;
        }
    case M_STRING:
        return strcmp(self->val->string, other->val->string);
    case M_TUPLE:
        if(self->val->size < other->val->size) {
            return -1;
        } else if(self->val->size > other->val->size) {
            return 1;
        } else {
            int i;
            for(i=0; i<self->val->size; i++) {
                int c;
                PyObject* c1 = Value2PyO(self->val->values[i]);
                PyObject* c2 = Value2PyO(other->val->values[i]);
                c = linda_Value_cmp((linda_ValueObject*)c1, (linda_ValueObject*)c2);
                Py_DECREF(c1); Py_DECREF(c2);
                if(c != 0) {
                    return c;
                }
            }
            return 0;
        }
    case M_SUM:
        {
        int c;
        PyObject* c1 = Value2PyO(self->val->value);
        PyObject* c2 = Value2PyO(other->val->value);
        c = linda_Value_cmp((linda_ValueObject*)c1, (linda_ValueObject*)c2);
        Py_DECREF(c1); Py_DECREF(c2);
        return c;
        }
    case M_FUNCTION:
        return strcmp(self->val->func_name, other->val->func_name);
    case M_TYPE:
        if(self->val->type_name != NULL && other->val->type_name != NULL && strcmp(self->val->type_name, other->val->type_name)) {
            return strcmp(self->val->type_name, other->val->type_name);
        } else if(self->val->type_spec != NULL && other->val->type_spec != NULL) {
            return Minimal_SyntaxTree_cmp(self->val->type_spec, other->val->type_spec);
        } else if(self->val->type_id > other->val->type_id) {
            return 1;
        } else if(self->val->type_id < other->val->type_id) {
            return -1;
        } else {
            return 0;
        }
    case M_TSREF:
        return strcmp(self->val->string, other->val->string);
    case M_POINTER:
        if(self->val->ptr < other->val->ptr) {
            return -1;
        } else if(self->val->ptr > other->val->ptr) {
            return 1;
        } else {
            return 0;
        }
    case M_SYNTAX_TREE:
        return Minimal_SyntaxTree_cmp(self->val->syntax_tree, other->val->syntax_tree);
    case M_BUILT_IN_FUNC:
        return strcmp(self->val->built_in_name, other->val->built_in_name);
    }
    return 0;
}

static PyObject* linda_Value_str(linda_ValueObject* self) {
    char* s = Minimal_Value_string(self->val);
    PyObject* o = PyString_FromFormat("%s", s);
    free(s);
    return o;
}

static PyObject* linda_Value_repr(linda_ValueObject* self) {
    char* s = Minimal_Value_string(self->val);
    PyObject* o = PyString_FromFormat("%s", s);
    free(s);
    return o;
}

static PyObject* linda_Value_setSumPos(linda_ValueObject* self, PyObject* args) {
    int i;

    if(!PyArg_ParseTuple(args, "i", &i)) {
        return NULL;
    }

    Linda_setSumPos(self->val, i);

    Py_INCREF(Py_None);
    return Py_None;
}

static PyObject* linda_Value_isType(linda_ValueObject* self) {
    if(Linda_isType(self->val)) {
        Py_INCREF(Py_True);
        return Py_True;
    } else {
        Py_INCREF(Py_False);
        return Py_False;
    }
}

static PyObject* linda_Value_isLong(linda_ValueObject* self) {
    if(Linda_isLong(self->val)) {
        Py_INCREF(Py_True);
        return Py_True;
    } else {
        Py_INCREF(Py_False);
        return Py_False;
    }
}

static PyObject* linda_Value_isString(linda_ValueObject* self) {
    if(Linda_isString(self->val)) {
        Py_INCREF(Py_True);
        return Py_True;
    } else {
        Py_INCREF(Py_False);
        return Py_False;
    }
}

static PyObject* linda_Value_isTuple(linda_ValueObject* self) {
    if(Linda_isTuple(self->val)) {
        Py_INCREF(Py_True);
        return Py_True;
    } else {
        Py_INCREF(Py_False);
        return Py_False;
    }
}

static PyObject* linda_Value_isTupleSpace(linda_ValueObject* self) {
    if(Linda_isTupleSpace(self->val)) {
        Py_INCREF(Py_True);
        return Py_True;
    } else {
        Py_INCREF(Py_False);
        return Py_False;
    }
}

static PyObject* linda_Value_isFunction(linda_ValueObject* self) {
    if(Linda_isFunction(self->val)) {
        Py_INCREF(Py_True);
        return Py_True;
    } else {
        Py_INCREF(Py_False);
        return Py_False;
    }
}

static PyObject* linda_Value_isPtr(linda_ValueObject* self) {
    if(Linda_isPtr(self->val)) {
        Py_INCREF(Py_True);
        return Py_True;
    } else {
        Py_INCREF(Py_False);
        return Py_False;
    }
}

/* These are all type related functions */

static PyObject* linda_Value_isNil(linda_ValueObject* self) {
    if(Linda_isType(self->val)) {
        if(self->val->type_spec == NULL) {
            Py_INCREF(Py_False);
            return Py_False;
        } else if(self->val->type_spec->type == ST_NIL) {
            Py_INCREF(Py_True);
            return Py_True;
        } else {
            Py_INCREF(Py_False);
            return Py_False;
        }
    } else {
        if(self->val->type == M_NIL) {
            Py_INCREF(Py_True);
            return Py_True;
        } else {
            Py_INCREF(Py_False);
            return Py_False;
        }
    }
}

static PyObject* linda_Value_isId(linda_ValueObject* self) {
    if(!Linda_isType(self->val)) {
        PyErr_SetString(PyExc_TypeError, "Value is not a type.\n");
        return NULL;
    }
    if(self->val->type_spec == NULL) {
        Py_INCREF(Py_False);
        return Py_False;
    } else if(self->val->type_spec->type == ST_IDENTIFIER) {
        Py_INCREF(Py_True);
        return Py_True;
    } else {
        Py_INCREF(Py_False);
        return Py_False;
    }
}

static PyObject* linda_Value_isProductType(linda_ValueObject* self) {
    if(!Linda_isType(self->val)) {
        PyErr_SetString(PyExc_TypeError, "Value is not a type.\n");
        return NULL;
    }
    if(self->val->type_spec == NULL) {
        Py_INCREF(Py_False);
        return Py_False;
    } else if(self->val->type_spec->type == ST_PRODUCT_TYPE) {
        Py_INCREF(Py_True);
        return Py_True;
    } else {
        Py_INCREF(Py_False);
        return Py_False;
    }
}

static PyObject* linda_Value_isSumType(linda_ValueObject* self) {
    if(!Linda_isType(self->val)) {
        PyErr_SetString(PyExc_TypeError, "Value is not a type.\n");
        return NULL;
    }
    if(self->val->type_spec == NULL) {
        Py_INCREF(Py_False);
        return Py_False;
    } else if(self->val->type_spec->type == ST_SUM_TYPE) {
        Py_INCREF(Py_True);
        return Py_True;
    } else {
        Py_INCREF(Py_False);
        return Py_False;
    }
}

static PyObject* linda_Value_isPtrType(linda_ValueObject* self) {
    if(!Linda_isType(self->val)) {
        PyErr_SetString(PyExc_TypeError, "Value is not a type.\n");
        return NULL;
    }
    if(self->val->type_spec == NULL) {
        Py_INCREF(Py_False);
        return Py_False;
    } else if(self->val->type_spec->type == ST_POINTER) {
        Py_INCREF(Py_True);
        return Py_True;
    } else {
        Py_INCREF(Py_False);
        return Py_False;
    }
}

static PyObject* linda_Value_isFunctionType(linda_ValueObject* self) {
    if(!Linda_isType(self->val)) {
        PyErr_SetString(PyExc_TypeError, "Value is not a type.\n");
        return NULL;
    }
    if(self->val->type_spec == NULL) {
        Py_INCREF(Py_False);
        return Py_False;
    } else if(self->val->type_spec->type == ST_TYPE_FUNCTION) {
        Py_INCREF(Py_True);
        return Py_True;
    } else {
        Py_INCREF(Py_False);
        return Py_False;
    }
}

static PyObject* linda_Value_deepcopy(linda_ValueObject* self, PyObject* args) {
    PyObject* r;
    LindaValue v = Linda_copy(self->val);
#ifdef USE_TYPES
    LindaValue type = Linda_copy(self->val->typeobj);
    Linda_setType(v, type);
    Linda_delReference(type);
#endif
    r = Value2PyO(v);
    Linda_delReference(v);
    return r;
}

static PyMethodDef value_methods[] = {
    {"setSumPos", (PyCFunction)linda_Value_setSumPos, METH_VARARGS, ""},
    {"isType", (PyCFunction)linda_Value_isType, METH_NOARGS, ""},
    {"isNil", (PyCFunction)linda_Value_isNil, METH_NOARGS, ""},
    {"isLong", (PyCFunction)linda_Value_isLong, METH_NOARGS, ""},
    {"isFunction", (PyCFunction)linda_Value_isFunction, METH_NOARGS, ""},
    {"isId", (PyCFunction)linda_Value_isId, METH_NOARGS, ""},
    {"isProductType", (PyCFunction)linda_Value_isProductType, METH_NOARGS, ""},
    {"isSumType", (PyCFunction)linda_Value_isSumType, METH_NOARGS, ""},
    {"isPtrType", (PyCFunction)linda_Value_isPtrType, METH_NOARGS, ""},
    {"isFunctionType", (PyCFunction)linda_Value_isFunctionType, METH_NOARGS, ""},
    {"isString", (PyCFunction)linda_Value_isString, METH_NOARGS, ""},
    {"isTuple", (PyCFunction)linda_Value_isTuple, METH_NOARGS, ""},
    {"isTupleSpace", (PyCFunction)linda_Value_isTupleSpace, METH_NOARGS, ""},
    {"isPtr", (PyCFunction)linda_Value_isPtr, METH_NOARGS, ""},
    {"__deepcopy__", (PyCFunction)linda_Value_deepcopy, METH_VARARGS, ""},
    {NULL}  /* Sentinel */
};

static PyObject* linda_Value_gettype(linda_ValueObject *self, void *closure) {
#ifdef TYPES
    LindaValue v = Linda_getType(self->val);
    if(v == NULL) {
        Py_INCREF(Py_None);
        return Py_None;
    } else {
        PyObject* o = Value2PyO(v);
        return o;
    }
#else
    switch(self->val->type) {
    case M_NIL:
        return Value2PyO(Linda_nilType);
    case M_BOOLEAN:
        return Value2PyO(Linda_boolType);
    case M_BYTE:
    case M_SHORT:
    case M_INTEGER:
    case M_LONG:
    case M_UBYTE:
    case M_USHORT:
    case M_UINTEGER:
    case M_ULONG:
        return Value2PyO(Linda_longType);
    case M_FLOAT:
    case M_DOUBLE:
        return Value2PyO(Linda_floatType);
    case M_STRING:
        return Value2PyO(Linda_stringType);
    case M_TYPE:
        return Value2PyO(Linda_typeType);
    case M_TSREF:
        return Value2PyO(Linda_tupleSpaceType);
    case M_TUPLE:
        Py_INCREF(Py_None);
        return Py_None;
    case M_FUNCTION:
        Py_INCREF(Py_None);
        return Py_None;
    case M_POINTER:
        Py_INCREF(Py_None);
        return Py_None;
    case M_SYNTAX_TREE:
        Py_INCREF(Py_None);
        return Py_None;
    }
    Py_INCREF(Py_None);
    return Py_None;
#endif
}

#ifdef TYPES
static int linda_Value_settype(linda_ValueObject* self, PyObject* value, void* closure) {
    LindaValue type;
    if(!PyObject_IsInstance(value, (PyObject*)&linda_ValueType)) { PyErr_SetString(PyExc_TypeError, "setType - Setting to not an value."); return -1; }

    type = PyO2Value(value);

    if(!Linda_isType(type)) { PyErr_SetString(PyExc_TypeError, "setType - Not a type."); Linda_delReference(type); return -1; }

    Linda_setType(self->val, type);
    Linda_delReference(type);
    return 0;
}
#endif

static PyObject* linda_Value_getid(linda_ValueObject *self, void *closure) {
    PyObject* string;

    if(!Linda_isType(self->val)) { PyErr_SetString(PyExc_TypeError, "getID - Not a type."); return NULL; }
    if(self->val->type_spec->type != ST_IDENTIFIER) { PyErr_SetString(PyExc_TypeError, "getID - Not an identifier."); return NULL; }
    string = PyString_FromString(self->val->type_spec->string);
    if(string == NULL) {
        PyErr_SetString(PyExc_TypeError, "Failed to get id string.");
        return NULL;
    } else {
        return string;
    }
}

static PyObject* linda_Value_getint(linda_ValueObject *self, void *closure) {
    if(!Linda_isLong(self->val)) { PyErr_SetString(PyExc_TypeError, "getInt - Not an integer."); return NULL; }
    return PyInt_FromLong(self->val->integer);
}

static PyObject* linda_Value_getstring(linda_ValueObject *self, void *closure) {
    if(!Linda_isString(self->val)) { PyErr_SetString(PyExc_TypeError, "getString - Not a string."); return NULL; }
    return PyString_FromString(Linda_getString(self->val));
}

static PyObject* linda_Value_getarg(linda_ValueObject *self, void *closure) {
    PyObject* o;
    LindaValue val;

    if(!Linda_isType(self->val)) { PyErr_SetString(PyExc_TypeError, "getArg - Not a type."); return NULL; }
    if(self->val->type_spec->type != ST_TYPE_FUNCTION) { PyErr_SetString(PyExc_TypeError, "getArg - Not a function type."); return NULL; }
    Minimal_addReference(self->val->type_spec->branches[0]);
    val = Minimal_typeSpec(self->val->type_name, self->val->type_spec->branches[0]);
    Linda_delReference((void*)(val->typemap));
    Linda_addReference((void*)(self->val->typemap));
    val->typemap = self->val->typemap;
    o = Value2PyO(val);
    Linda_delReference(val);
    return o;
}

static PyObject* linda_Value_getresult(linda_ValueObject *self, void *closure) {
    PyObject* o;
    LindaValue val;

    if(!Linda_isType(self->val)) { PyErr_SetString(PyExc_TypeError, "getResult - Not a type."); return NULL; }
    if(self->val->type_spec->type != ST_TYPE_FUNCTION) { PyErr_SetString(PyExc_TypeError, "getResult - Not a function type."); return NULL; }
    Minimal_addReference(self->val->type_spec->branches[1]);
    val = Minimal_typeSpec(self->val->type_name, self->val->type_spec->branches[1]);
    Linda_delReference((void*)(val->typemap));
    Linda_addReference((void*)(self->val->typemap));
    val->typemap = self->val->typemap;
    o = Value2PyO(val);
    Linda_delReference(val);
    return o;
}

static PyObject* linda_Value_getfunc_name(linda_ValueObject *self, void *closure) {
    if(!Linda_isFunction(self->val)) { PyErr_SetString(PyExc_TypeError, "getFunc_Name - Not a function."); return NULL; }

    return PyString_FromString(self->val->func_name);
}

#ifdef TYPES
static PyObject* linda_Value_gettypemap(linda_ValueObject *self, void *closure) {
    linda_TypeMapObject* map;
    if(!Linda_isType(self->val)) { PyErr_SetString(PyExc_TypeError, "getTypeMap - Not a type."); return NULL; }

    map = (linda_TypeMapObject*)PyObject_CallFunction((PyObject*)&linda_TypeMapType, "", 0);

    if(Linda_isType(self->val)) {
        Linda_addReference((void*)(self->val->typemap));
        map->map = self->val->typemap;
    } else {
        Linda_addReference((void*)(self->val->typeobj->typemap));
        map->map = self->val->typeobj->typemap;
    }

    return (PyObject*)map;
}

static PyObject* linda_Value_gettypeid(linda_ValueObject* self, void* closure) {
    if(!Linda_isType(self->val)) {
        PyErr_SetString(PyExc_TypeError, "getTypeID - Not a type.");
        return NULL;
    }

    if(self->val->type_id == NULL) {
        Py_INCREF(Py_None);
        return Py_None;
    } else {
        return PyString_FromString(self->val->type_id);
    }
}

static PyObject* linda_Value_getidtypeid(linda_ValueObject* self, void* closure) {
    if(!Linda_isType(self->val)) {
        PyErr_SetString(PyExc_TypeError, "getTypeID - Not a type.");
        return NULL;
    }

    if(self->val->type_spec->type_id == NULL) {
        Py_INCREF(Py_None);
        return Py_None;
    } else {
        return PyString_FromString(self->val->type_spec->type_id);
    }
}

static int linda_Value_setidtypeid(linda_ValueObject* self, PyObject* value, void* closure) {
    if(!PyString_Check(value)) { PyErr_SetString(PyExc_TypeError, "setTypeID - Setting to not an string."); return -1; }
    if(!Linda_isType(self->val)) {
        PyErr_SetString(PyExc_TypeError, "setTypeID - Not a type.");
        return -1;
    }

    self->val->type_spec->type_id = malloc(strlen(PyString_AsString(value)) + 1);
    strcpy(self->val->type_spec->type_id, PyString_AsString(value));

    return 0;
}

static int linda_Value_settypeid(linda_ValueObject* self, PyObject* value, void* closure) {
    if(!PyString_Check(value)) { PyErr_SetString(PyExc_TypeError, "setTypeID - Setting to not an string."); return -1; }

    if(Linda_isType(self->val)) {
        self->val->type_id = malloc(strlen(PyString_AsString(value)) + 1);
        strcpy(self->val->type_id, PyString_AsString(value));
    } else {
        LindaValue type = Minimal_typeFromId(PyString_AsString(value));
        Linda_setType(self->val, type);
        Linda_delReference(type);
    }

    return 0;
}

static PyObject* linda_Value_getsumpos(linda_ValueObject* self, void* closure) {
    return PyInt_FromLong(Linda_getSumPos(self->val));
}

static int linda_Value_setsumpos(linda_ValueObject* self, PyObject* value, void* closure) {
    if(!PyInt_Check(value)) { PyErr_SetString(PyExc_TypeError, "setSumPos - Setting to not an integer."); return -1; }

    Linda_setSumPos(self->val, PyInt_AsLong(value));
    return 0;
}

static PyObject* linda_Value_gettype_name(linda_ValueObject* self, void* closure) {
    if(!Linda_isType(self->val)) {
        PyErr_SetString(PyExc_TypeError, "getTypeName- Not a type.");
        return NULL;
    }

    if(self->val->type_name != NULL) {
        return PyString_FromString(self->val->type_name);
    } else {
        Py_INCREF(Py_None);
        return Py_None;
    }
}

static PyObject* linda_Value_getptr(linda_ValueObject* self, void* closure) {
    if(!Linda_isPtr(self->val)) {
        PyErr_SetString(PyExc_TypeError, "getptr - Not a pointer.");
        return NULL;
    }

    if(Linda_getPtr(self->val) != NULL) {
        return Value2PyO(Linda_getPtr(self->val));
    } else {
        Py_INCREF(Py_None);
        return Py_None;
    }
}

static int linda_Value_setptr(linda_ValueObject* self, PyObject* value, void* closure) {
    if(!Linda_isPtr(self->val)) {
        PyErr_SetString(PyExc_TypeError, "setptr - Not a pointer.");
        return 1;
    }

    if(value == Py_None) {
        Linda_setPtr(self->val, NULL);
    } else {
        Linda_setPtr(self->val, PyO2Value(value));
    }
    return 0;
}

static PyObject* linda_Value_getptrtype(linda_ValueObject* self, void* closure) {
    if(!Linda_isType(self->val) || self->val->type_spec->type != ST_POINTER) {
        PyErr_SetString(PyExc_TypeError, "getptrtype - Not a pointer type.");
        return NULL;
    }

    return PyString_FromString(self->val->type_spec->ptr);
}

static PyObject* linda_Value_getvalue(linda_ValueObject* self, void* closure) {
    if(!Linda_isSum(self->val)) {
        PyErr_SetString(PyExc_TypeError, "getvalue - Not a Sum.");
        return NULL;
    }

    return Value2PyO(Linda_getSumValue(self->val));
}

static int linda_Value_setvalue(linda_ValueObject* self, PyObject* value, void* closure) {
    if(!Linda_isSum(self->val)) {
        PyErr_SetString(PyExc_TypeError, "setvalue - Not a Sum.");
        return 1;
    }

    Linda_setSumValue(self->val, PyO2Value(value), Linda_getSumPos(self->val));
    return 0;
}
#endif

static PyObject* linda_Value_gettsid(linda_ValueObject *self, void *closure) {
    if(!Linda_isTupleSpace(self->val)) { PyErr_SetString(PyExc_TypeError, "getTSID - Not a tuplespace."); return NULL; }
    return PyString_FromString(Linda_getTupleSpace(self->val));
}

static PyGetSetDef value_getseters[] = {
    {"id", (getter)linda_Value_getid, (setter)NULL, "", NULL},
    {"int", (getter)linda_Value_getint, (setter)NULL, "", NULL},
    {"string", (getter)linda_Value_getstring, (setter)NULL, "", NULL},
    {"arg", (getter)linda_Value_getarg, (setter)NULL, "", NULL},
    {"result", (getter)linda_Value_getresult, (setter)NULL, "", NULL},
    {"func_name", (getter)linda_Value_getfunc_name, (setter)NULL, "", NULL},
    {"tsid", (getter)linda_Value_gettsid, (setter)NULL, "", NULL},
#ifdef TYPES
    {"type", (getter)linda_Value_gettype, (setter)linda_Value_settype, "", NULL},
    {"type_name", (getter)linda_Value_gettype_name, (setter)NULL, "", NULL},
    {"typemap", (getter)linda_Value_gettypemap, (setter)NULL, "", NULL},
    {"type_id", (getter)linda_Value_gettypeid, (setter)linda_Value_settypeid, "", NULL},
    {"id_type_id", (getter)linda_Value_getidtypeid, (setter)linda_Value_setidtypeid, "", NULL},
    {"sum_pos", (getter)linda_Value_getsumpos, (setter)linda_Value_setsumpos, "", NULL},
    {"ptr", (getter)linda_Value_getptr, (setter)linda_Value_setptr, "", NULL},
    {"ptrtype", (getter)linda_Value_getptrtype, (setter)NULL, "", NULL},
#else
    {"type", (getter)linda_Value_gettype, (setter)NULL, "", NULL},
#endif
    {"value", (getter)linda_Value_getvalue, (setter)linda_Value_setvalue, "", NULL},
    {NULL}  /* Sentinel */
};

static Py_ssize_t linda_Value_len(linda_ValueObject *self) {
    if(Linda_isType(self->val)) {
        return self->val->type_spec->length;
    } else if(Linda_isString(self->val)) {
        return strlen(self->val->string);
    } else if(Linda_isTuple(self->val)) {
        return Linda_getTupleSize(self->val);
    } else {
        PyErr_SetString(PyExc_TypeError, "Taking length of a type without a length.");
        return -1;
    }
}

static PyObject* linda_Value_item(linda_ValueObject *self, Py_ssize_t index) {
    PyObject* o;
    switch(self->val->type) {
    case M_TYPE:
        {
        if(self->val->type_spec->type != ST_PRODUCT_TYPE && self->val->type_spec->type != ST_SUM_TYPE) {
            PyErr_SetString(PyExc_TypeError, "Not a product or sum type.");
            return NULL;
        } else if(index >= self->val->type_spec->length) {
            PyErr_SetString(PyExc_IndexError, "Index out of range for type.");
            return NULL;
        }
        LindaValue val = Minimal_typeSpec(self->val->type_name, self->val->type_spec->branches[index]);
        Minimal_addReference(self->val->type_spec->branches[index]);
        Linda_delReference((MinimalObject)(val->typemap));
        Linda_addReference((MinimalObject)(self->val->typemap));
        val->typemap = self->val->typemap;
        o = Value2PyO(val);
        Linda_delReference(val);
        return o;
        }
    case M_TUPLE:
        {
        LindaValue v = Linda_tupleGet(self->val, index);
        if(v == NULL) {
            PyErr_SetString(PyExc_IndexError, "Index out of range for tuple.");
            return NULL;
        } else {
            return Value2PyO(v);
        }
        }
    case M_STRING:
        if(index < 0 || index >= strlen(self->val->string)) {
            PyErr_SetString(PyExc_IndexError, "Index out of range for string.");
            return NULL;
        } else {
            return Py_BuildValue("c", self->val->string[index]);
        }
    default:
        PyErr_SetObject(PyExc_TypeError, PyString_FromFormat("Getting item from a type with is a single element %i.", self->val->type));
        return NULL;
    }
}

static int linda_Value_assign_item(linda_ValueObject *self, Py_ssize_t index, PyObject* value) {
    switch(self->val->type) {
    case M_TUPLE:
        {
        LindaValue v = PyO2Value(value);
        Linda_tupleSet(self->val, index, v);
        return 0;
        }
    default:
        PyErr_SetObject(PyExc_TypeError, PyString_FromFormat("Setting item for a type with is a single element %i.", self->val->type));
        return 1;
    }
}

PySequenceMethods linda_ValueSeq = {
        (lenfunc)linda_Value_len,  /* lenfunc sq_length; */
        0,                /*binaryfunc sq_concat; */
        0,                /*ssizeargfunc sq_repeat; */
        (ssizeargfunc)linda_Value_item, /*ssizeargfunc sq_item; */
        0,                /*ssizessizeargfunc sq_slice; */
        (ssizeobjargproc)linda_Value_assign_item,                /*ssizeobjargproc sq_ass_item; */
        0,                /*ssizessizeobjargproc sq_ass_slice; */
        0,                /*objobjproc sq_contains; */
        0,                /*binaryfunc sq_inplace_concat; */
        0,                /*ssizeargfunc sq_inplace_repeat; */
};

static PyObject* linda_Value_add(linda_ValueObject* self, linda_ValueObject* other) {
    if(Linda_isLong(self->val) && Linda_isLong(other->val)) {
        return PyInt_FromLong(Linda_getLong(self->val) + Linda_getLong(other->val));
    } else {
        PyErr_SetString(PyExc_TypeError, "Addition to value by invalid type.");
        return NULL;
    }
}

static PyObject* linda_Value_sub(linda_ValueObject* self, linda_ValueObject* other) {
    if(Linda_isLong(self->val) && Linda_isLong(other->val)) {
        return PyInt_FromLong(Linda_getLong(self->val) - Linda_getLong(other->val));
    } else {
        PyErr_SetString(PyExc_TypeError, "Subtraction from value by invalid type.");
        return NULL;
    }
}

static PyObject* linda_Value_remainder(linda_ValueObject* self, linda_ValueObject* other) {
    if(Linda_isLong(self->val) && Linda_isLong(other->val)) {
        return PyInt_FromLong(Linda_getLong(self->val) % Linda_getLong(other->val));
    } else {
        PyErr_SetString(PyExc_TypeError, "Remainder of value by invalid type.");
        return NULL;
    }
}

static int linda_Value_nonzero(linda_ValueObject* self) {
    if(self->val->type == M_NIL) {
        return 0;
    } else if(self->val->type == M_BOOLEAN) {
        if(self->val->boolean == 0) {
            return 0;
        } else {
            return 1;
        }
    } else if(self->val->type == M_INTEGER) {
        if(self->val->integer == 0) {
            return 0;
        } else {
            return 1;
        }
    } else if(self->val->type == M_STRING) {
        if(strlen(self->val->string) == 0) {
            return 0;
        } else {
            return 1;
        }
    } else if(self->val->type == M_TUPLE) {
        if(self->val->size == 0) {
            return 0;
        } else {
            return 1;
        }
    } else if(self->val->type == M_FUNCTION) {
        return 1;
    } else if(self->val->type == M_TYPE) {
        return 1;
    } else {
        PyErr_SetObject(PyExc_TypeError, PyString_FromFormat("PyLibLinda: Unknown type (%i) in nb_nonzero.\n", self->val->type));
        return -1;
    }
}

static PyObject* linda_Value_nbint(linda_ValueObject* self) {
    if(Linda_isLong(self->val)) {
        return PyInt_FromLong(Linda_getLong(self->val));
    } else {
        PyErr_SetString(PyExc_TypeError, "Intification of value of an invalid type.");
        return NULL;
    }
}

static int linda_Value_coerce(PyObject** self, PyObject** other) {
    if(PyInt_Check(*other)) {
        LindaValue l = Linda_long(PyInt_AsLong(*other));
        *other = Value2PyO(l);
        Linda_delReference(l)
        Py_INCREF(*self);
        return 0;
    } else if(PyInt_Check(*self)) {
        LindaValue l = Linda_long(PyInt_AsLong(*other));
        *self = Value2PyO(l);
        Linda_delReference(l)
        Py_INCREF(*other);
        return 0;
    } else {
        return 1;
    }
}

static long linda_ValueHash(linda_ValueObject* self) {
    switch(self->val->type) {
    case M_NIL:
        return 0;
    case M_BOOLEAN:
        if(self->val->boolean) {
            return PyObject_Hash(Py_True);
        } else {
            return PyObject_Hash(Py_False);
        }
    case M_INTEGER:
        {
        PyObject* r = PyInt_FromLong(self->val->integer);
        long hash = PyObject_Hash(r);
        Py_DecRef(r);
        return hash;
        }
    case M_UINTEGER:
        {
        PyObject* r = PyInt_FromLong(self->val->integer);
        long hash = PyObject_Hash(r);
        Py_DecRef(r);
        return hash;
        }
    case M_FLOAT:
        {
        PyObject* r = PyFloat_FromDouble(self->val->singlefloat);
        long hash = PyObject_Hash(r);
        Py_DecRef(r);
        return hash;
        }
    case M_DOUBLE:
        {
        PyObject* r = PyFloat_FromDouble(self->val->doublefloat);
        long hash = PyObject_Hash(r);
        Py_DecRef(r);
        return hash;
        }
    case M_STRING:
        {
        PyObject* r = PyString_FromStringAndSize(self->val->string, self->val->length);
        long hash = PyObject_Hash(r);
        Py_DecRef(r);
        return hash;
        }
    case M_TYPE:
        {
        PyObject* r;
        if(self->val->type_name != NULL) {
            r = PyString_FromString(self->val->type_name);
        } else {
            r = PyString_FromString(self->val->type_id);
        }
        long hash = PyObject_Hash(r);
        Py_DecRef(r);
        return hash;
        }
    case M_TSREF:
        {
        PyObject* r = PyString_FromString(self->val->tsid);
        long hash = PyObject_Hash(r);
        Py_DecRef(r);
        return hash;
        }
    case M_TUPLE:
        {
        PyObject* r = PyInt_FromLong((long)(self->val));
        long hash = PyObject_Hash(r);
        Py_DecRef(r);
        return hash;
        }
    case M_SUM:
        {
        PyObject* r = Value2PyO(self->val->value);
        long hash = PyObject_Hash(r);
        Py_DecRef(r);
        return hash;
        }
    case M_FUNCTION:
        {
        PyObject* r = PyInt_FromLong((long)(self->val));
        long hash = PyObject_Hash(r);
        Py_DecRef(r);
        return hash;
        }
    case M_POINTER:
        {
        PyObject* r = PyInt_FromLong((long)(self->val));
        long hash = PyObject_Hash(r);
        Py_DecRef(r);
        return hash;
        }
    case M_SYNTAX_TREE:
        {
        PyObject* r = PyInt_FromLong((long)(self->val));
        long hash = PyObject_Hash(r);
        Py_DecRef(r);
        return hash;
        }
    case M_BUILT_IN_FUNC:
        {
        PyObject* r = PyString_FromString(self->val->built_in_name);
        long hash = PyObject_Hash(r);
        Py_DecRef(r);
        return hash;
        }
    }
    return -1;
}

PyObject* linda_ValueCall(linda_ValueObject* self, PyObject* args, PyObject* kwargs) {
    LindaValue largs;
    LindaValue r;
    PyObject* result;

    if(!Linda_isFunction(self->val)) {
        PyErr_SetString(PyExc_TypeError, "Not a function.");
        return NULL;
    }

    largs = PyO2Value(args);

    r = Linda_apply(self->val, largs);

    result = Value2PyO(r);

    Linda_delReference(largs);
    Linda_delReference(r);

    return result;
}

PyNumberMethods linda_ValueNum = {
        (binaryfunc)linda_Value_add,  /* binaryfunc nb_add; */
        (binaryfunc)linda_Value_sub,  /* binaryfunc nb_subtract; */
        0,  /* binaryfunc nb_multiply; */
        0,  /* binaryfunc nb_divide; */
        (binaryfunc)linda_Value_remainder,  /* binaryfunc nb_remainder; */
        0,  /* binaryfunc nb_divmod; */
        0,  /* ternaryfunc nb_power; */
        0,  /* unaryfunc nb_negative; */
        0,  /* unaryfunc nb_positive; */
        0,  /* unaryfunc nb_absolute; */
        (inquiry)linda_Value_nonzero,  /* inquiry nb_nonzero; */
        0,  /* unaryfunc nb_invert; */
        0,  /* binaryfunc nb_lshift; */
        0,  /* binaryfunc nb_rshift; */
        0,  /* binaryfunc nb_and; */
        0,  /* binaryfunc nb_xor; */
        0,  /* binaryfunc nb_or; */
        (coercion)linda_Value_coerce,  /* coercion nb_coerce; */
        (unaryfunc)linda_Value_nbint,  /* unaryfunc nb_int; */
        0,  /* unaryfunc nb_long; */
        0,  /* unaryfunc nb_float; */
        0,  /* unaryfunc nb_oct; */
        0,  /* unaryfunc nb_hex; */
        /* Added in release 2.0 */
        0,  /* binaryfunc nb_inplace_add; */
        0,  /* binaryfunc nb_inplace_subtract; */
        0,  /* binaryfunc nb_inplace_multiply; */
        0,  /* binaryfunc nb_inplace_divide; */
        0,  /* binaryfunc nb_inplace_remainder; */
        0,  /* ternaryfunc nb_inplace_power; */
        0,  /* binaryfunc nb_inplace_lshift; */
        0,  /* binaryfunc nb_inplace_rshift; */
        0,  /* binaryfunc nb_inplace_and; */
        0,  /* binaryfunc nb_inplace_xor; */
        0,  /* binaryfunc nb_inplace_or; */
        /* Added in release 2.2 */
        /* The following require the Py_TPFLAGS_HAVE_CLASS flag */
        0,  /* binaryfunc nb_floor_divide; */
        0,  /* binaryfunc nb_true_divide; */
        0,  /* binaryfunc nb_inplace_floor_divide; */
        0,  /* binaryfunc nb_inplace_true_divide; */
#if (PY_VERSION_HEX >= 0x02050000)
        /* Added in release 2.5 */
        0,  /* unaryfunc nb_index; */
#endif
};

PyTypeObject linda_ValueType = {
    PyObject_HEAD_INIT(NULL)
    0,                         /*ob_size*/
    "linda.Value",        /*tp_name*/
    sizeof(linda_ValueObject), /*tp_basicsize*/
    0,                         /*tp_itemsize*/
    (destructor)linda_Value_dealloc,  /*tp_dealloc*/
    0,                         /*tp_print*/
    0,                         /*tp_getattr*/
    0,                         /*tp_setattr*/
    (cmpfunc)linda_Value_cmp,  /*tp_compare*/
    (reprfunc)linda_Value_repr,/*tp_repr*/
    &linda_ValueNum,           /*tp_as_number*/
    &linda_ValueSeq,           /*tp_as_sequence*/
    0,                         /*tp_as_mapping*/
    (hashfunc)linda_ValueHash, /*tp_hash */
    (ternaryfunc)linda_ValueCall, /*tp_call*/
    (reprfunc)linda_Value_str, /*tp_str*/
    0,                         /*tp_getattro*/
    0,                         /*tp_setattro*/
    0,                         /*tp_as_buffer*/
    Py_TPFLAGS_DEFAULT,        /*tp_flags*/
    "A Linda Value",           /* tp_doc */
    0,                         /* tp_traverse; */
    0,                         /* tp_clear; */
    0,                         /* tp_richcompare; */
    0,                         /* tp_weaklistoffset; */
    0,                         /* tp_iter; */
    0,                         /* tp_iternext; */
    value_methods,             /* tp_methods; */
    0,                         /* tp_members */
    value_getseters,           /* tp_getset */
    0,                         /* tp_base */
    0,                         /* tp_dict */
    0,                         /* tp_descr_get */
    0,                         /* tp_descr_set */
    0,                         /* tp_dictoffset */
    (initproc)linda_Value_init, /* tp_init */
    0,                         /* tp_alloc */
    linda_Value_new,      /* tp_new */
};

LindaValue PyO2Value(PyObject* obj) {
    if(PyObject_IsInstance(obj, (PyObject*)&linda_ValueType)) {
        Linda_addReference(((linda_ValueObject*)obj)->val);
        return ((linda_ValueObject*)obj)->val;
    } else {
        PyObject* o = PyObject_CallFunctionObjArgs((PyObject*)&linda_ValueType, obj, NULL);
        if(PyErr_Occurred()) { return NULL; }
#ifdef TYPES
        if(o == NULL && !Linda_is_server) {
            PyObject* func = NULL;
            PyObject* type;
            PyErr_Clear();
            type = PyObject_GetAttrString(obj, "__class__");
            if(type != NULL) {
                func = LindaPython_lookupConvertTo(type);
            }
            if(type == NULL || func == NULL) {
                LindaValue t = LindaPython_lookupType(obj);
                if(type != NULL) { Py_DECREF(type); }
                if(t == NULL) {
                    PyErr_SetString(PyExc_SystemError, "Failed to create Value from Python Object.");
                    return NULL;
                } else {
                    return t;
                }
            } else {
                PyObject* converted;
                LindaValue v;
                LindaValue t = LindaPython_lookupType(type);
                Py_DECREF(type);
                if(t == NULL) { return NULL; }
                PyObject* memo = PyObject_CallFunction((PyObject*)&linda_MemoType, "");
                ((linda_MemoObject*)memo)->convert_to = 1;
                converted = PyObject_CallFunction(func, "OO", obj, memo);
                Py_DECREF(memo);
                Py_DECREF(func);
                if(converted == NULL) { return NULL; }
                v = PyO2Value(converted);
                if(v != NULL) {
                    Linda_setType(v, t);
                }
                return v;
            }
        } else {
#endif
            LindaValue v;
            Linda_addReference(((linda_ValueObject*)o)->val);
            v = ((linda_ValueObject*)o)->val;
            Py_DECREF(o);
            return v;
#ifdef TYPES
        }
#endif
    }
}

linda_MemoObject* memo = NULL;
PyObject* Value2PyO(LindaValue obj) {
    if(!Linda_is_server && Linda_isTupleSpace(obj)) {
        return PyObject_CallFunction((PyObject*)&linda_TupleSpaceType, "s", Linda_getTupleSpace(obj));
#ifdef USE_TYPES
    } else if(!Linda_is_server) {
        PyObject* func = NULL;
        PyObject* type = (PyObject*)((&linda_ValueType)->tp_alloc(&linda_ValueType, 0));
        if(type == NULL) {
            PyErr_SetString(PyExc_SystemError, "Failed to create Python Object from Value.");
            return NULL;
        }
        ((linda_ValueObject*)type)->val = Linda_getType(obj);
        if(((linda_ValueObject*)type)->val != NULL) {
            func = LindaPython_lookupConvertFrom(type);
        }
        if(func != NULL) {
            unsigned char newmemo;
            PyObject* converted;
            Linda_addReference(obj);
            ((linda_ValueObject*)type)->val = obj;
            if(memo == NULL) {
                memo = (linda_MemoObject*)PyObject_CallFunction((PyObject*)&linda_MemoType, "");
                memo->convert_to = 0;
                newmemo = 1;
            } else {
                newmemo = 0;
                converted = PyObject_CallMethod((PyObject*)memo, "getReal", "O", type);
                if(converted != NULL) {
                    Py_DECREF(type); Py_DECREF(func);
                    return converted;
                }
                PyErr_Clear();
            }
            converted = PyObject_CallFunction(func, "OO", type, (PyObject*)memo);
            if(newmemo) {
                Py_DECREF(memo); memo = NULL;
            }
            Py_DECREF(type);
            Py_DECREF(func);
            return converted;
        } else if(((linda_ValueObject*)type)->val != NULL) {
            PyObject* _class = LindaPython_lookupClass(type);
            Py_DECREF(type);
            if(_class != NULL) {
                Py_INCREF(_class);
                return _class;
            }
        } else {
            Py_DECREF(type);
        }
#endif
    }

    PyObject* o = (PyObject*)((&linda_ValueType)->tp_alloc(&linda_ValueType, 0));
    if(o == NULL) {
        PyErr_SetString(PyExc_SystemError, "Failed to create Python Object from Value.");
        return NULL;
    }
    Linda_addReference(obj);
    ((linda_ValueObject*)o)->val = obj;
    return o;
}

PyObject* Tuple2PyO(LindaValue t) {
    int i;
    PyObject* pyt = PyTuple_New(Linda_getTupleSize(t));

    for(i=0; i<Linda_getTupleSize(t); i++) {
        PyObject* v = Value2PyO(Linda_tupleGet(t, i));
        if(v == NULL) {
            Py_DECREF(pyt);
            return NULL;
        }
        PyTuple_SetItem(pyt, i, v);
    }

    return pyt;
}

LindaValue PyO2Tuple(PyObject* tup) {
    int i;
    LindaValue t = Linda_tuple(PyTuple_Size(tup));
    for(i=0; i<PyTuple_Size(tup); i++) {
        LindaValue v;
        v = PyO2Value(PyTuple_GetItem(tup, i));
        if(v == NULL) { Linda_delReference(t); return NULL; }
        Linda_tupleSet(t, i, v);
    }
    return t;
}

void initvalue(PyObject* m) {
    linda_ValueType.tp_new = PyType_GenericNew;
    if (PyType_Ready(&linda_ValueType) < 0)
        return;

    Py_INCREF(&linda_ValueType);
    PyModule_AddObject(m, "Value", (PyObject*)&linda_ValueType);
}
