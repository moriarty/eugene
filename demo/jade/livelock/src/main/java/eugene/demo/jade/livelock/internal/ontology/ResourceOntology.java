/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.demo.jade.livelock.internal.ontology;

import jade.content.onto.BeanOntology;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;

/**
 * Ontology specification for requesting and surrendering resources.
 *
 * @author Jakub D Kozlowski
 * @since 0.1.3
 */
public class ResourceOntology extends BeanOntology {

    /**
     * Singleton instance.
     */
    private static final ResourceOntology instance = new ResourceOntology();

    /**
     * Name of this ontology.
     */
    public static final String NAME = "request-resource-ontology";

    /**
     * Private constructor.
     */
    private ResourceOntology() {
        super(NAME);

        try {
            add(RequestAction.class);
            add(Resource.class);
            add(SurrenderAction.class);
        }
        catch (OntologyException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the singleton instance of this <code>ResourceOntology</code>.
     *
     * @return Singleton instance of this <code>ResourceOntology</code>.
     */
    public static Ontology getInstance() {
        return instance;
    }
}
