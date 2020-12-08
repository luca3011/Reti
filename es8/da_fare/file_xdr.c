/*
 * Please do not edit this file.
 * It was generated using rpcgen.
 */

#include "file.h"

bool_t
xdr_Output (XDR *xdrs, Output *objp)
{
	register int32_t *buf;

	 if (!xdr_int (xdrs, &objp->caratteri))
		 return FALSE;
	 if (!xdr_int (xdrs, &objp->parole))
		 return FALSE;
	 if (!xdr_int (xdrs, &objp->linee))
		 return FALSE;
	return TRUE;
}

bool_t
xdr_Input (XDR *xdrs, Input *objp)
{
	register int32_t *buf;

	 if (!xdr_string (xdrs, &objp->nome, 50))
		 return FALSE;
	 if (!xdr_int (xdrs, &objp->soglia))
		 return FALSE;
	return TRUE;
}
