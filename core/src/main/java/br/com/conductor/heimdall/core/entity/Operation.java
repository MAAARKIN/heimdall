/*
 * Copyright (C) 2018 Conductor Tecnologia SA
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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
 */
package br.com.conductor.heimdall.core.entity;

import br.com.conductor.heimdall.core.enums.HttpMethod;
import br.com.conductor.heimdall.core.util.ConstantsPath;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;

/**
 * This class represents a Operation registered to the system.
 *
 * @author Filipe Germano
 * @author Marcelo Aguiar Rodrigues
 */
@Data
@EqualsAndHashCode(of = { "id" })
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@RedisHash("operation")
public class Operation implements Serializable {

     private static final long serialVersionUID = -7728017075091653564L;

     @Id
     private String id;

     @Indexed
     private HttpMethod method;

     @Indexed
     private String path;

     private String description;

     @Indexed
     private String resourceId;

     @Indexed
     private String apiId;

     /**
      * Adjust the path to not permit the save or update with "/" or spaces in the end of path.
      */
     public void fixBasePath() {
          this.path = this.path.trim();
          if (this.path.endsWith(ConstantsPath.PATH_ROOT)) {
               this.path = path.substring(0, path.length()-1);
          }
     }

}