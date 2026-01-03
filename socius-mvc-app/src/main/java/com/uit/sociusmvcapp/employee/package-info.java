@ApplicationModule(
    displayName = "Employee Module",
    allowedDependencies = {
      "iam",
      "iam :: dto",
      "iam :: enum",
      "shared",
      "azure",
      "azure :: blob",
      "azure :: graph"
    })
package com.uit.sociusmvcapp.employee;

import org.springframework.modulith.ApplicationModule;
