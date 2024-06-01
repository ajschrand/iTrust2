package edu.ncsu.csc.iTrust2.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

/**
 * Represents an Email stored by the iTrust2 system (and not sent through
 * official email systems)
 *
 * @author Kai Presler-Marshall
 *
 */
@Entity
public class Email extends DomainObject {

    /**
     * ID of this email
     */
    @Id
    @GeneratedValue ( strategy = GenerationType.AUTO )
    private Long   id;

    /**
     * Sender of this Email. String so that it can come from the system (no
     * user)
     */
    @NotNull
    private String sender;

    /** Receiver of the email. Must be a user in the system */
    @NotNull
    @ManyToOne
    @JoinColumn ( name = "receiver", columnDefinition = "varchar(100)" )
    private User   receiver;

    /** Subject of the email */
    @NotNull
    private String subject;

    /** Email message body */
    @NotNull
    private String messageBody;

    /**
     * Creates an Email object
     *
     * @param sender
     *            Sender of the email
     * @param receiver
     *            Receiver of the email
     * @param subject
     *            Subject of the email
     * @param messageBody
     *            Message of the email
     */
    public Email ( final String sender, final User receiver, final String subject, final String messageBody ) {
        this.sender = sender;
        this.receiver = receiver;
        this.subject = subject;
        this.messageBody = messageBody;
    }

    /**
     * For Spring/Hibernate
     */
    public Email () {

    }

    /**
     * Gets the ID of this email
     */
    @Override
    public Long getId () {
        return id;
    }

    /**
     * Gets the Sender of this email
     *
     * @return Sender
     */
    public String getSender () {
        return sender;
    }

    /**
     * Sets the sender of this email
     *
     * @param sender
     *            New sender
     */
    public void setSender ( final String sender ) {
        this.sender = sender;
    }

    /**
     * Gets the Receiver of this email
     *
     * @return The receiver of the email
     */
    public User getReceiver () {
        return receiver;
    }

    /**
     * Sets the receiver of this email
     *
     * @param receiver
     *            New receiver
     */
    public void setReceiver ( final User receiver ) {
        this.receiver = receiver;
    }

    /**
     * Gets the Subject of this email
     *
     * @return Subject
     */
    public String getSubject () {
        return subject;
    }

    /**
     * Sets the Subject of this email
     *
     * @param subject
     *            New subject
     */
    public void setSubject ( final String subject ) {
        this.subject = subject;
    }

    /**
     * Gets the message body
     *
     * @return Message body
     */
    public String getMessageBody () {
        return messageBody;
    }

    /**
     * Sets the message body
     *
     * @param messageBody
     *            New message body
     */
    public void setMessageBody ( final String messageBody ) {
        this.messageBody = messageBody;
    }

}
