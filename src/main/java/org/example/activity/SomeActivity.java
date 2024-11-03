package org.example.activity;

import org.example.model.ImmutableResponseObject;
import org.example.model.RequestObject;
import org.example.model.ResponseObject;

import javax.inject.Inject;

public class SomeActivity {
    @Inject
    public SomeActivity() {}

    public ResponseObject activityEntrypoint(final RequestObject requestObject) {
        return ImmutableResponseObject.builder().build();
    }
}
