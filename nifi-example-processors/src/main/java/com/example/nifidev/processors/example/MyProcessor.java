/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.nifidev.processors.example;

import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.annotation.behavior.ReadsAttribute;
import org.apache.nifi.annotation.behavior.ReadsAttributes;
import org.apache.nifi.annotation.behavior.WritesAttribute;
import org.apache.nifi.annotation.behavior.WritesAttributes;
import org.apache.nifi.annotation.lifecycle.OnScheduled;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.SeeAlso;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.processor.AbstractProcessor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.ProcessorInitializationContext;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.processor.util.StandardValidators;
import org.apache.nifi.processor.io.InputStreamCallback;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

@Tags({"example"})
@CapabilityDescription("It checks whether string is pallindrome")
@SeeAlso({})
@ReadsAttributes({@ReadsAttribute(attribute="", description="")})
@WritesAttributes({@WritesAttribute(attribute="", description="")})
public class MyProcessor extends AbstractProcessor {

    public static final PropertyDescriptor MY_PROPERTY = new PropertyDescriptor
            .Builder().name("MY_PROPERTY")
            .displayName("My property")
            .description("Example Property")
            .required(true)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .build();

    public static final Relationship RELATION_MATCHED = new Relationship.Builder()
            .name("RELATION_MATCHED")
            .description("Matched relationship")
            .build();
    
    public static final Relationship RELATION_UNMATCHED = new Relationship.Builder()
            .name("RELATION_UNMATCHED")
            .description("Unmatched relationship")
            .build();

    private List<PropertyDescriptor> descriptors;

    private Set<Relationship> relationships;

    @Override
    protected void init(final ProcessorInitializationContext context) {
        final List<PropertyDescriptor> descriptors = new ArrayList<>();
        descriptors.add(MY_PROPERTY);
        this.descriptors = Collections.unmodifiableList(descriptors);

        final Set<Relationship> relationships = new HashSet<>();
        relationships.add(RELATION_MATCHED);
        relationships.add(RELATION_UNMATCHED);
        this.relationships = Collections.unmodifiableSet(relationships);
    }

    @Override
    public Set<Relationship> getRelationships() {
        return this.relationships;
    }

    @Override
    public final List<PropertyDescriptor> getSupportedPropertyDescriptors() {
        return descriptors;
    }

    @OnScheduled
    public void onScheduled(final ProcessContext context) {

    }

    @Override
    public void onTrigger(final ProcessContext context, final ProcessSession session) throws ProcessException {
    	final AtomicReference<Boolean> isPallindrome= new AtomicReference<>();
    	
        FlowFile flowFile = session.get();
        if ( flowFile == null ) {
            return;
        }
        // TODO implement
        session.read(flowFile, new InputStreamCallback() {
        	
        	public Boolean pallindromeCheck(String keyword) {
        		int length = keyword.length();
                int middle = length/2;
        		for(int i=0; i < middle;i++) {
        			if(keyword.charAt(i) != keyword.charAt(length-i-1)) {
        				return false;
        			}
        		}
        		return true;
        	}
        	
			@Override
			public void process(InputStream inputStream) throws IOException {
				// TODO Auto-generated method stub
				String originalContent = new BufferedReader(new InputStreamReader(inputStream)).readLine();
				isPallindrome.set(pallindromeCheck(originalContent));
			}
		});
        
        if(isPallindrome.get()) {
        	session.transfer(flowFile, RELATION_MATCHED);
        } else {
        	session.transfer(flowFile, RELATION_UNMATCHED);
        }     
    }
}
