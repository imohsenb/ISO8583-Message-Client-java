package com.imohsenb.ISO8583.interfaces;

import com.imohsenb.ISO8583.builders.GeneralMessageClassBuilder;

/**
 * @author Mohsen Beiranvand
 */
public interface MessageClass {
    /**
     * Determine if funds are available, get an approval but do not post to account for reconciliation.
     * @return
     */
    MessagePacker<GeneralMessageClassBuilder> authorization();

    /**
     * Determine if funds are available, get an approval and post directly to the account.
     * @return
     */
    MessagePacker<GeneralMessageClassBuilder> financial();

    /**
     * Used for hot-card, TMS and other exchanges
     * @return
     */
    MessagePacker<GeneralMessageClassBuilder> fileAction();

    /**
     * Reverses the action of a previous authorization.
     * @return
     */
    MessagePacker<GeneralMessageClassBuilder> reversal();

    /**
     * Transmits settlement information label.
     * @return
     */
    MessagePacker<GeneralMessageClassBuilder> reconciliation();

    /**
     * Transmits administrative advice.
     * @return
     */
    MessagePacker<GeneralMessageClassBuilder> administrative();
    MessagePacker<GeneralMessageClassBuilder> feeCollection();

    /**
     * Used for secure key exchange, logon, echo test and other network functions
     * @return
     */
    MessagePacker<GeneralMessageClassBuilder> networkManagement();
}
