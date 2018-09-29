package de.andreasgerhard.exceptgen.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Generated in Project exception-logging-generator for
 *
 * @author angerhar
 * @version %$LastChangedRevision:  $version:  %
 * @noa.created_by %$LastChangedBy: angerhar $created_by: angerhar %
 * @noa.date_created %$LastChangedDate:  %
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class MasterException {


    private String masterPackageName;
    private String masterClassName;
    private String masterInheritClassName;


}
