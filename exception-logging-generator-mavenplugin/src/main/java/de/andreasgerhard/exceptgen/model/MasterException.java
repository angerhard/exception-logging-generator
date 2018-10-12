package de.andreasgerhard.exceptgen.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class MasterException {


    private String masterPackageName;
    private String masterClassName;
    private String masterInheritClassName;


}
