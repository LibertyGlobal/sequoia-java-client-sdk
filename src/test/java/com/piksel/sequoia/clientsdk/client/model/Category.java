package com.piksel.sequoia.clientsdk.client.model;

/*-
 * #%L
 * Sequoia Java Client SDK
 * %%
 * Copyright (C) 2018 Piksel
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.google.api.client.util.Key;
import com.piksel.sequoia.clientsdk.resource.IndirectRelationship;
import com.piksel.sequoia.clientsdk.resource.Resource;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Category extends Resource {
    
    @Key
    private String value;
    
    @Key
    private String scheme;

    @IndirectRelationship(ref = "categoryRefs", relationship = "contents")
    private List<Optional<Content>> contents;
    
    @IndirectRelationship(ref = "categoryRefs", relationship = "contents")
    private Collection<Content> contentsCollection;
}
