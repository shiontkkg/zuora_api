package com.example.zuora_api.retry;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.client.ApiException;
import org.openapitools.client.model.ErrorResponse;
import com.example.zuora_api.config.ZuoraV2ApiRetryPolicy;
import com.example.zuora_api.config.ZuoraV2Client;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ZuoraV2ApiRetryPolicyTest {

    @Mock
    private ZuoraV2Client zuoraClient;

    private ZuoraV2ApiRetryPolicy target;

    @BeforeEach
    void setUp() {
        this.target = new ZuoraV2ApiRetryPolicy(5, zuoraClient);
    }

    /**
     * 最大リトライ回数に達するまではリトライ可能と判定する。
     *
     * @param count 現在のリトライ回数
     * @param expected リトライ判定
     */
    @ParameterizedTest
    @CsvSource({
            "0, true",
            "1, true",
            "4, true",
            "5, false"
    })
    void testCanRetry_リトライ回数(int count, boolean expected) {

        // 準備
        var context = target.open(null);

        var apiException = new ApiException(500, "");
        var response = new ErrorResponse();
        response.setRetryable(true);
        apiException.setErrorObject(response);

        for (int i = 0; i < count; i++) {
            target.registerThrowable(context, apiException);
        }

        // 実行
        var actual = target.canRetry(context);

        // 検証
        assertEquals(expected, actual);
    }

    /**
     * 最大リトライ回数に達するまではリトライ可能と判定する。
     *
     * @param count 現在のリトライ回数
     * @param expected リトライ判定
     */
    @ParameterizedTest
    @CsvSource({
            "1, 401, true",
            "4, 401, true",
            "5, 401, false"
    })
    void testCanRetry_ステータスコード(int count, int code, boolean expected) {

        // 準備
        var context = target.open(null);

        var apiException = new ApiException(code, "");
        var response = new ErrorResponse();
        response.setRetryable(true);
        apiException.setErrorObject(response);

        for (int i = 0; i < count; i++) {
            target.registerThrowable(context, apiException);
        }

        // 実行
        var actual = target.canRetry(context);

        // 検証
        assertEquals(expected, actual);
        verify(zuoraClient, times(1)).retryAuth();
    }

    /**
     * ApiExceptionのretryableによってリトライ可能かを判定する。
     *
     * @param retryable
     * @param expected
     */
    @ParameterizedTest
    @CsvSource({
            "true, true",
            "false, false"
    })
    void testCanRetry_retryable(boolean retryable, boolean expected) {

        // 準備
        var context = target.open(null);

        var apiException = new ApiException(500, "");
        var response = new ErrorResponse();
        response.setRetryable(retryable);
        apiException.setErrorObject(response);
        target.registerThrowable(context, apiException);

        // 実行
        var actual = target.canRetry(context);

        // 検証
        assertEquals(expected, actual);
    }
}
