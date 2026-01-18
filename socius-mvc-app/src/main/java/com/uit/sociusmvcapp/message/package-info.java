@ApplicationModule(
    displayName = "Message Module",
    allowedDependencies = {"shared", "iam", "azure :: blob"})
package com.uit.sociusmvcapp.message;

import org.springframework.modulith.ApplicationModule;
