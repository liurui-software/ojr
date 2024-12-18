package com.ojr.informix;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;

public class OnstatCommandExecutorTest {

    private static OnstatCommandExecutor onstatCommandExecutor;

    @BeforeAll
    public static void init() {
        onstatCommandExecutor = new OnstatCommandExecutor("dbPath", "server");
    }

    @Test
    public void shouldExecuteCommand() {
        assertTrue(onstatCommandExecutor.executeCommand("script.sh").isPresent());
    }

}

