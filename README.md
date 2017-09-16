# exception-logging-generator
Java code generator for exceptions and logging
 
###### Which problem has to be solved?
Plan your class design considering security, error handling, logging and i18n frondend messages.
You have to write a xml file containing class name and messages (frontend, backend). This tool 
generates the exception code. 
###### A small scripple as big picture



###### Design security and transparency

Anyone who logs in to your system uses a wrong password, 
an expired account or without any roles to use your application should be informed.
To prevent an security hole, you will report in every case 
"Member name or password wrong" (or "HALT! Zugang verboten!" in german), 
for the backend you want to report any information about the incident.
These informations are separated in this framework.

###### Microservice relevant

The exception will generate a json payload containing all messages in every language you have defined.
This payload can be used by the frontend to retrieve the correct message.   

##### Examples

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<messages
        masterException="com.andreasgerhard.exception.BusinessException"
        masterExceptionInherit="java.lang.RuntimeException"
        default="en">
    <message name="Customer" domain="CUSTOMER.001">
        <exception package="com.andreasgerhard.exception"
                   inherit="com.andreasgerhard.exception.BusinessRuntimeException" returnCode="500"/>
        <frontendMessages>
            <frontendMessage locale="de">Es ist ein Fehler bei der Kundenbearbeitung aufgetreten.</frontendMessage>
            <frontendMessage locale="en">An error has been occured during a customer operation.</frontendMessage>
        </frontendMessages>
        <backendMessage>General failure, please check cause.</backendMessage>
    </message>
    <message name="CustomerNotFound" domain="CUSTOMER.002">
        <exception package="com.andreasgerhard.exception"
                   inherit="CustomerException"
                   returnCode="404"/>
        <frontendMessages>
            <frontendMessage locale="de">Der Kunde mit der ID {id:Integer} wurde nicht gefunden.</frontendMessage>
            <frontendMessage locale="en">A customer with id {id:Integer} could not be found.</frontendMessage>
        </frontendMessages>
        <backendMessage>Customer not found with ID {id:Integer}.</backendMessage>
    </message>
    <message name="CustomerCreated" domain="CUSTOMER.003">
        <frontendMessages>
            <frontendMessage locale="de">Ein neuer Kunde mit der ID {id:Integer} wurde angelegt.</frontendMessage>
            <frontendMessage locale="en">Created a new customer with id {id:Integer}.</frontendMessage>
        </frontendMessages>
        <backendMessage>A new customer has been created with id {id:Integer} and name {name:String}.</backendMessage>
    </message>
    <message name="CustomerLoggedIn" domain="CUSTOMER.004">
        <exception package="com.andreasgerhard.exception"
                   inherit="CustomerException"
                   returnCode="404"/>
        <frontendMessages>
            <frontendMessage locale="de">Der Kunde mit der ID {id:Integer} konnte sich nicht einloggen. Fehlermeldung System: {message:String}; Code {code:Long}.</frontendMessage>
            <frontendMessage locale="en">A customer with id {id:Integer} could not logged in. Error message {message:String}; Code {code:Long}.</frontendMessage>
        </frontendMessages>
        <backendMessage>Customer not found with ID {id:Integer}.</backendMessage>
    </message>
</messages>
```
will lead to 
![alt text](https://raw.githubusercontent.com/angerhard/exception-logging-generator/develop/exception-logging-generator.png "Project structure")

