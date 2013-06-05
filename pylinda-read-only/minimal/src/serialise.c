/*
*    Copyright 2006 Andrew Wilkinson <aw@cs.york.ac.uk>.
*
*    This file is part of Minimal (http://www-users.cs.york.ac.uk/~aw/pylinda)
*
*    Minimal is free software; you can redistribute it and/or modify
*    it under the terms of the GNU Lesser General Public License as published by
*    the Free Software Foundation; either version 2.1 of the License, or
*    (at your option) any later version.
*
*    Minimal is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU Lesser General Public License for more details.
*
*    You should have received a copy of the GNU Lesser General Public License
*    along with Minimal; if not, write to the Free Software
*    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

#include <string.h>
#include <stdlib.h>

#include <libxml/xmlmemory.h>
#include <libxml/xmlsave.h>

#include "minimal_internal.h"

#ifdef WIN32
#define snprintf _snprintf
#endif

void Minimal_serialiseParameterList(xmlDocPtr doc, xmlNodePtr parent, struct Minimal_SyntaxTree_t* parameter_list);
void Minimal_serialiseTypeSpec(xmlDocPtr doc, xmlNodePtr parent, struct Minimal_SyntaxTree_t* type_spec, MinimalLayer typemap);

xmlDocPtr Minimal_serialiseXML(xmlDocPtr doc, xmlNodePtr parent, MinimalValue f, unsigned char include_type, unsigned char include_type_spec) {
    MinimalValue* memo;

    if(doc == NULL) {
        doc = xmlNewDoc(NULL);
    }
    if(parent == NULL) {
        parent = xmlNewDocNode(doc, NULL, (xmlChar*)"minimal", NULL);
        xmlDocSetRootElement(doc, parent);
    }

    memo = malloc(sizeof(void*)*2);
    memo[0] = NULL;

    Minimal_serialiseValue(doc, parent, parent, f, &memo, include_type, include_type_spec, 1);

    free(memo);

    return doc;
}

char* Minimal_serialise(MinimalValue f, unsigned char include_type, unsigned char include_type_spec) {
    xmlChar* buf;
    int size;
    char* outbuf;

    xmlDocPtr doc = Minimal_serialiseXML(NULL, NULL, f, include_type, include_type_spec);

    xmlDocDumpFormatMemory(doc, &buf, &size, 1);

    outbuf = (char*)malloc(size+1);
    memcpy(outbuf, buf, size);
    outbuf[size] = '\0';

    xmlFreeDoc(doc);
    xmlFree(buf);

    return outbuf;
}

void Minimal_addTypesToList(Minimal_TypeList* list, MinimalValue v) {
    if(v->typeobj != NULL) {
        if(Minimal_addTypeToTypeList(list, v->typeobj) && v->typeobj->type_spec != NULL) {
            Minimal_getTypeList2(v->typeobj->type_spec, v->typeobj->typemap, list);
        }
    }

    switch(v->type) {
    case M_NIL:
    case M_BOOLEAN:
    case M_INTEGER:
    case M_UINTEGER:
    case M_FLOAT:
    case M_DOUBLE:
    case M_STRING:
        break;
    case M_TYPE:
        if(Minimal_addTypeToTypeList(list, v)) {
            Minimal_getTypeList2(v->type_spec, v->typemap, list);
        }
        break;
    case M_TUPLE:
        {
        int i;
        for(i = 0; i < Minimal_getTupleSize(v); i++) {
            Minimal_addTypesToList(list, Minimal_tupleGet(v, i));
        }
        }
        break;
    case M_SUM:
        Minimal_addTypesToList(list, v->value);
        break;
    case M_FUNCTION:
    case M_POINTER:
    case M_TSREF:
    case M_SYNTAX_TREE:
    case M_BUILT_IN_FUNC:
        break;
    }
}

#define AddTypeName(x) if(Minimal_use_types) { \
                       if(include_type) { \
                        Minimal_includeTypes(doc, x, f); \
                       } \
                       if(f->typeobj != NULL) { \
                        if(f->typeobj->type_id == NULL) { \
                         xmlNewProp(x, (xmlChar*)"typename", (xmlChar*)f->typeobj->type_name); \
                        } else if(include_type_id) { \
                         xmlNewProp(x, (xmlChar*)"typeid", (xmlChar*)f->typeobj->type_id); \
                        } \
                       } \
                       }
#define AddSumPos(x) if(f->sum_pos != -1) { \
                        char* pos = (char*)malloc(snprintf(NULL, 0, "%i", f->sum_pos)+1); \
                        sprintf(pos, "%i", f->sum_pos); \
                        xmlNewProp(x, (xmlChar*)"sum_pos", (xmlChar*)pos); \
                        free(pos); \
                     }
#define AddId(x)     { \
                        char* id = (char*)malloc(snprintf(NULL, 0, "%li", (unsigned long)f)+1); \
                        sprintf(id, "%li", (unsigned long)f); \
                        xmlNewProp(x, (xmlChar*)"id", (xmlChar*)id); \
                        free(id); \
                     }

void Minimal_includeTypes(xmlDocPtr doc, xmlNodePtr parent, MinimalValue f) {
    Minimal_TypeList list;
    int i;

    if((f->type == M_TYPE && f->type_id !=0) || (f->typeobj != NULL && f->typeobj->type_id != 0)) {
        return;
    }
    if(f->type == M_TUPLE && f->typeobj == NULL) {
        int i;
        int m = 0;
        for(i = 0; i < Minimal_getTupleSize(f); i++) {
            MinimalValue v = Minimal_tupleGet(f, i);

            if(v == NULL || (v->type == M_TYPE && v->type_id != 0) || (v->typeobj != NULL && v->typeobj->type_id != 0)) {
                m++;
            }
        }
        if(m == i) {
            return;
        }
    }

    list = malloc(sizeof(void*));
    list[0] = NULL;

    Minimal_addTypesToList(&list, f);

    for(i = 0; list[i] != NULL; i++) {
        xmlNodePtr typenode = xmlNewDocNode(doc, NULL, (xmlChar*)"type", NULL);
        xmlAddChild(parent, typenode);
        xmlNewProp(typenode, (xmlChar*)"name", (xmlChar*)list[i]->type_name);
        if(list[i]->typeobj != NULL) {
            xmlNewProp(typenode, (xmlChar*)"typename", (xmlChar*)list[i]->typeobj->type_name);
        }
        Minimal_serialiseTypeSpec(doc, typenode, list[i]->type_spec, list[i]->typemap);
    }

    Minimal_freeTypeList(list);
}

void Minimal_serialiseValue(xmlDocPtr doc, xmlNodePtr root, xmlNodePtr parent, MinimalValue f, MinimalValue** memo, unsigned char include_type, unsigned char include_type_spec, unsigned char include_type_id) {
    int i;
    MinimalValue* newmemo;

    for(i = 0; (*memo)[i] != NULL; i++) { }
    newmemo = malloc(sizeof(void*)*(i+2));
    memcpy(newmemo, (*memo), sizeof(void*)*i);
    newmemo[i] = f;
    newmemo[i+1] = NULL;
    free(*memo);
    *memo = newmemo;

    switch(f->type) {
    case M_NIL:
        {
        xmlNodePtr node = xmlNewDocNode(doc, NULL, (xmlChar*)"nil", NULL);
        xmlAddChild(parent, node);
        AddTypeName(node);
        AddId(node);
        return;
        }
    case M_BOOLEAN:
        {
        xmlNodePtr node;
        if(f->boolean) {
            node = xmlNewDocNode(doc, NULL, (xmlChar*)"true", NULL);
        } else {
            node = xmlNewDocNode(doc, NULL, (xmlChar*)"false", NULL);
        }
        AddTypeName(node);
        AddId(node);
        return;
        }
    case M_INTEGER:
        {
        char* integer;
        xmlNodePtr node = xmlNewDocNode(doc, NULL, (xmlChar*)"integer", NULL);
        xmlAddChild(parent, node);
        AddTypeName(node);
        AddId(node);
        integer = (char*)malloc(snprintf(NULL, 0, "%li", f->integer)+1);
        sprintf(integer, "%li", f->integer);
        xmlNewProp(node, (xmlChar*)"val", (xmlChar*)integer);
        free(integer);
        return;
        }
    case M_UINTEGER:
        {
        char* integer;
        xmlNodePtr node = xmlNewDocNode(doc, NULL, (xmlChar*)"uinteger", NULL);
        xmlAddChild(parent, node);
        AddTypeName(node);
        AddId(node);
        integer = (char*)malloc(snprintf(NULL, 0, "%li", f->integer)+1);
        sprintf(integer, "%li", f->integer);
        xmlNewProp(node, (xmlChar*)"val", (xmlChar*)integer);
        free(integer);
        return;
        }
    case M_FLOAT:
        {
        char* floating;
        xmlNodePtr node = xmlNewDocNode(doc, NULL, (xmlChar*)"float", NULL);
        xmlAddChild(parent, node);
        AddTypeName(node);
        AddId(node);
        floating = (char*)malloc(snprintf(NULL, 0, "%f", f->singlefloat)+1);
        sprintf(floating, "%f", f->singlefloat);
        xmlNewProp(node, (xmlChar*)"val", (xmlChar*)floating);
        free(floating);
        return;
        }
    case M_DOUBLE:
        {
        char* floating;
        xmlNodePtr node = xmlNewDocNode(doc, NULL, (xmlChar*)"float", NULL);
        xmlAddChild(parent, node);
        AddTypeName(node);
        AddId(node);
        floating = (char*)malloc(snprintf(NULL, 0, "%f", f->doublefloat)+1);
        sprintf(floating, "%f", f->doublefloat);
        xmlNewProp(node, (xmlChar*)"val", (xmlChar*)floating);
        free(floating);
        return;
        }
    case M_STRING:
        {
        xmlNodePtr node = xmlNewTextChild(parent, NULL, (xmlChar*)"string", (xmlChar*)f->string);
        xmlAddChild(parent, node);
        AddTypeName(node);
        AddId(node);
        return;
        }
    case M_TYPE:
        {
        xmlNodePtr typenode;
        AddTypeName(parent);
        typenode = xmlNewDocNode(doc, NULL, (xmlChar*)"typeobj", NULL);
        xmlAddChild(parent, typenode);
        AddId(typenode);
        xmlNewProp(typenode, (xmlChar*)"name", (xmlChar*)f->type_name);
        if(f->type_id == NULL || include_type_spec) {
            Minimal_serialiseTypeSpec(doc, typenode, f->type_spec, f->typemap);
        } else {
            xmlNewProp(typenode, (xmlChar*)"typeid", (xmlChar*)f->type_id);
        }
        return;
        }
    case M_TUPLE:
        {
        int i;
        xmlNodePtr node = xmlNewDocNode(doc, NULL, (xmlChar*)"tuple", NULL);
        xmlAddChild(parent, node);
        AddTypeName(node);
        AddId(node);
        if(f->typeobj != NULL && f->typeobj->type_id != NULL) {
            include_type_id = 0;
        }
        for(i=0; i<f->size; i++) {
            xmlNodePtr e;
            if(f->values[i] == NULL) { break; }
            e = xmlNewDocNode(doc, NULL, (xmlChar*)"element", NULL);
            xmlAddChild(node, e);
            Minimal_serialiseValue(doc, root, e, f->values[i], memo, 0, 0, include_type_id);
        }
        return;
        }
    case M_SUM:
        {
        char* sumpos;
        xmlNodePtr node = xmlNewTextChild(parent, NULL, (xmlChar*)"sum", (xmlChar*)f->string);
        xmlAddChild(parent, node);
        AddTypeName(node);
        AddSumPos(node);
        AddId(node);
        sumpos = (char*)malloc(snprintf(NULL, 0, "%li", (unsigned long)f->sum_pos)+1);
        sprintf(sumpos, "%li", (unsigned long)f->sum_pos);
        xmlNewProp(node, (xmlChar*)"val", (xmlChar*)sumpos);
        free(sumpos);
        Minimal_serialiseValue(doc, root, node, f->value, memo, 0, 0, include_type_id);
        return;
        }
    case M_FUNCTION:
        {
        Minimal_serialiseFunction(doc, parent, f, memo, 0);
        return;
        }
    case M_POINTER:
        {
        int i;
        char* ptrval;
        MinimalValue* newmemo;
        xmlNodePtr node = xmlNewDocNode(doc, NULL, (xmlChar*)"ptr", NULL);
        xmlAddChild(parent, node);
        AddTypeName(node);
        AddId(node);
        ptrval = (char*)malloc(snprintf(NULL, 0, "%li", (unsigned long)f->integer)+1);
        sprintf(ptrval, "%li", (unsigned long)f->ptr);
        xmlNewProp(node, (xmlChar*)"val", (xmlChar*)ptrval);
        free(ptrval);

        for(i = 0; (*memo)[i] != NULL; i++) {
            if((*memo)[i] == f->ptr) { return; }
        }
        newmemo = malloc(sizeof(void*)*(i+2));
        memcpy(newmemo, (*memo), sizeof(void*)*i);
        newmemo[i] = f->ptr;
        newmemo[i+1] = NULL;
        free(*memo);
        *memo = newmemo;

        Minimal_serialiseValue(doc, root, root, f->ptr, memo, 0, 0, include_type_id);

        return;
        }
    case M_TSREF:
        {
        xmlNodePtr node = xmlNewTextChild(parent, NULL, (xmlChar*)"tsid", (xmlChar*)f->string);
        xmlAddChild(parent, node);
        AddTypeName(node);
        AddId(node);
        }
        break;
    case M_SYNTAX_TREE:
        fprintf(stderr, "Error: Should never get here. (%s:%i)\n", __FILE__, __LINE__);
        break;
    case M_BUILT_IN_FUNC:
        {
        xmlNodePtr node = xmlNewTextChild(parent, NULL, (xmlChar*)"builtin", (xmlChar*)f->built_in_name);
        AddTypeName(node);
        AddId(node);
        }
        break;
    }
}

void Minimal_serialiseTypeSpec(xmlDocPtr doc, xmlNodePtr parent, struct Minimal_SyntaxTree_t* type_spec, MinimalLayer typemap) {
    if(type_spec == NULL) { return; }
    switch(type_spec->type) {
    case ST_BLANK:
        {
        return;
        }
    case ST_NIL:
        {
        xmlNodePtr node = xmlNewDocNode(doc, NULL, (xmlChar*)"nil", NULL);
        xmlAddChild(parent, node);
        return;
        }
    case ST_IDENTIFIER:
        {
        xmlNodePtr node;
        if(type_spec->type_id != NULL) {
            xmlNodePtr node = xmlNewDocNode(doc, NULL, (xmlChar*)"id", NULL);
            xmlAddChild(parent, node);
            xmlNewProp(node, (xmlChar*)"name", (xmlChar*)type_spec->string);
            xmlNewProp(node, (xmlChar*)"typeid", (xmlChar*)type_spec->type_id);
            return;
        }
        MinimalValue v = Minimal_getName(typemap, type_spec->string);
        if(v != NULL && v->type_id != 0) {
            MinimalValue type;
            xmlNodePtr node = xmlNewDocNode(doc, NULL, (xmlChar*)"id", NULL);
            xmlAddChild(parent, node);
            xmlNewProp(node, (xmlChar*)"name", (xmlChar*)type_spec->string);
            type = Minimal_getName(typemap, type_spec->string);
            if(type != NULL) {
                xmlNewProp(node, (xmlChar*)"typeid", (xmlChar*)type->type_id);
                Minimal_delReference(type);
            }
        } else {
            node = xmlNewDocNode(doc, NULL, (xmlChar*)"id", NULL);
            xmlAddChild(parent, node);
            xmlNewProp(node, (xmlChar*)"name", (xmlChar*)type_spec->string);
        }
        if(v != NULL) {
            Minimal_delReference(v);
        }
        break;
        }
    case ST_TYPE_SPEC:
        {
        Minimal_serialiseTypeSpec(doc, parent, type_spec->type_def, typemap);
        return;
        }
    case ST_TYPE_FUNCTION:
        {
        xmlNodePtr node = xmlNewDocNode(doc, NULL, (xmlChar*)"type_function", NULL);
        xmlAddChild(parent, node);
        Minimal_serialiseTypeSpec(doc, node, type_spec->branches[0], typemap);
        Minimal_serialiseTypeSpec(doc, node, type_spec->branches[1], typemap);
        return;
        }
    case ST_PRODUCT_TYPE:
        {
        int i;
        xmlNodePtr node = xmlNewDocNode(doc, NULL, (xmlChar*)"product_type", NULL);
        xmlAddChild(parent, node);
        for(i = 0; i < type_spec->length; i++) {
            Minimal_serialiseTypeSpec(doc, node, type_spec->branches[i], typemap);
        }
        return;
        }
    case ST_SUM_TYPE:
        {
        int i;
        xmlNodePtr node = xmlNewDocNode(doc, NULL, (xmlChar*)"sum_type", NULL);
        xmlAddChild(parent, node);
        for(i = 0; i < type_spec->length; i++) {
            Minimal_serialiseTypeSpec(doc, node, type_spec->branches[i], typemap);
        }
        return;
        }
    case ST_POINTER:
        {
        xmlNodePtr node = xmlNewDocNode(doc, NULL, (xmlChar*)"ptr", NULL);
        xmlAddChild(parent, node);
        xmlNewProp(node, (xmlChar*)"name", (xmlChar*)type_spec->ptr);
        return;
        }
    case ST_BRACKET:
        {
        Minimal_serialiseTypeSpec(doc, parent, type_spec->type_def, typemap);
        return;
        }
    case ST_PARAMETER_LIST:
        {
        Minimal_serialiseParameterList(doc, parent, type_spec);
        return;
        }
    default:
        fprintf(stderr, "Error: Unknown syntax tree id in Minimal_serialiseTypeSpec (%i).\n", type_spec->type);
        return;
    }
}

void Minimal_serialiseParameterList(xmlDocPtr doc, xmlNodePtr parent, struct Minimal_SyntaxTree_t* parameter_list) {
    xmlNodePtr node = xmlNewDocNode(doc, NULL, (xmlChar*)"parameter_list", NULL);
    xmlAddChild(parent, node);

    while(parameter_list != NULL) {
        xmlNodePtr p = xmlNewDocNode(doc, NULL, (xmlChar*)"param", NULL);
        xmlAddChild(node, p);
        xmlNewProp(p, (xmlChar*)"name", (xmlChar*)parameter_list->var_name);

        parameter_list = parameter_list->next_var;
    }
}

void Minimal_serialiseCode(xmlDocPtr doc, xmlNodePtr parent, struct Minimal_SyntaxTree_t* tree) {
    switch(tree->type) {
    case ST_BLANK:
        {
        return;
        }
    case ST_IDENTIFIER:
        {
        xmlNodePtr node = xmlNewDocNode(doc, NULL, (xmlChar*)"id", NULL);
        xmlAddChild(parent, node);
        xmlNewProp(node, (xmlChar*)"name", (xmlChar*)tree->string);
        return;
        }
    case ST_INTEGER:
        {
        char* integer;
        xmlNodePtr node = xmlNewDocNode(doc, NULL, (xmlChar*)"integer", NULL);
        xmlAddChild(parent, node);
        integer = (char*)malloc(snprintf(NULL, 0, "%i", tree->integer)+1);
        sprintf(integer, "%i", tree->integer);
        xmlNewProp(node, (xmlChar*)"val", (xmlChar*)integer);
        return;
        }
    case ST_STRING:
        {
        xmlNodePtr node = xmlNewDocNode(doc, NULL, (xmlChar*)"string", NULL);
        xmlAddChild(parent, node);
        xmlNewProp(node, (xmlChar*)"val", (xmlChar*)tree->string);
        return;
        }
    case ST_OPERATOR:
        {
        xmlNodePtr p = xmlNewDocNode(doc, NULL, (xmlChar*)"operator", NULL);
        xmlAddChild(parent, p);
        xmlNewProp(p, (xmlChar*)"op", (xmlChar*)tree->_operator);
        Minimal_serialiseCode(doc, p, tree->op1);
        Minimal_serialiseCode(doc, p, tree->op2);
        return;
        }
    case ST_INDEX:
        {
        xmlNodePtr p = xmlNewDocNode(doc, NULL, (xmlChar*)"index", NULL);
        xmlAddChild(parent, p);
        Minimal_serialiseCode(doc, p, tree->expr);
        Minimal_serialiseCode(doc, p, tree->index);
        return;
        }
    case ST_LET:
        {
        xmlNodePtr p = xmlNewDocNode(doc, NULL, (xmlChar*)"let", NULL);
        xmlAddChild(parent, p);
        Minimal_serialiseCode(doc, p, tree->var);
        Minimal_serialiseCode(doc, p, tree->letexpr);
        Minimal_serialiseCode(doc, p, tree->code);
        return;
        }
    case ST_FUNCTION_CALL:
        {
        xmlNodePtr p = xmlNewDocNode(doc, NULL, (xmlChar*)"funccall", NULL);
        xmlAddChild(parent, p);
        Minimal_serialiseCode(doc, p, tree->function);
        Minimal_serialiseCode(doc, p, tree->arguments);
        return;
        }
    case ST_ARGUMENT_LIST:
        {
        xmlNodePtr p = xmlNewDocNode(doc, NULL, (xmlChar*)"argument_list", NULL);
        xmlAddChild(parent, p);
        Minimal_serialiseCode(doc, p, tree->argument);
        if(tree->next_arg != NULL) {
            Minimal_serialiseCode(doc, p, tree->next_arg);
        }
        return;
        }
    case ST_TUPLE:
        {
        int i;
        char* integer;

        xmlNodePtr p = xmlNewDocNode(doc, NULL, (xmlChar*)"tuple", NULL);
        integer = (char*)malloc(snprintf(NULL, 0, "%i", tree->size)+1);
        sprintf(integer, "%i", tree->size);
        xmlNewProp(p, (xmlChar*)"size", (xmlChar*)integer);
        free(integer);
        xmlAddChild(parent, p);
        for(i=0; i<tree->size; i++) {
            Minimal_serialiseCode(doc, p, tree->tuple[i]);
        }
        return;
        }
    default:
        fprintf(stderr, "Error: Unknown syntax tree id in Minimal_serializeCode (%i).\n", tree->type);
        return;
    }
}

void Minimal_serialiseFunction(xmlDocPtr doc, xmlNodePtr parent, MinimalValue f, MinimalValue** memo, unsigned char include_type) {
    unsigned char include_type_id = 1;
    xmlNodePtr type_spec;
    xmlNodePtr code;
    xmlNodePtr node = xmlNewDocNode(doc, NULL, (xmlChar*)"function", NULL);
    xmlNewProp(node, (xmlChar*)"name", (xmlChar*)f->func_name);
    xmlAddChild(parent, node);
    AddTypeName(node);
    AddId(node);

    type_spec = xmlNewDocNode(doc, NULL, (xmlChar*)"type", NULL);
    xmlAddChild(node, type_spec);
    xmlNewProp(type_spec, (xmlChar*)"name", (xmlChar*)f->func_name);
    Minimal_serialiseTypeSpec(doc, type_spec, f->type_spec, f->typemap);

    Minimal_serialiseParameterList(doc, node, f->parameter_list);

    code = xmlNewDocNode(doc, NULL, (xmlChar*)"code", NULL);
    xmlAddChild(node, code);
    Minimal_serialiseCode(doc, code, f->code);
}

