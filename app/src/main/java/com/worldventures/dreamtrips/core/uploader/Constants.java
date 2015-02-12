/*
 * Copyright 2010-2014 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.worldventures.dreamtrips.core.uploader;

import com.worldventures.dreamtrips.BuildConfig;

public class Constants {

//    public static final String AWS_ACCOUNT_ID = "360783003803";
//    public static final String COGNITO_POOL_ID = "us-east-1:1a3cd42a-77b1-4a47-80d7-c39a39e6e688";
//    public static final String COGNITO_ROLE_UNAUTH = "arn:aws:iam::360783003803:role/Cognito_dreamtripsdevUnauth_DefaultRole";
//
//    public static final String BUCKET_NAME = "dtuploadingtest";

    public static final String AWS_ACCOUNT_ID = BuildConfig.AWS_ACCOUNT_ID;
    public static final String COGNITO_POOL_ID = BuildConfig.COGNITO_POOL_ID;
    public static final String COGNITO_ROLE_UNAUTH = BuildConfig.COGNITO_ROLE_UNAUTH;

    public static final String BUCKET_NAME = BuildConfig.BUCKET_NAME;
    public static final String BUCKET_ROOT_PATH = BuildConfig.BUCKET_ROOT_PATH;
}
