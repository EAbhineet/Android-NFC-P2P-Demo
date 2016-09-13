package p2pdemo.nfc.pmgdroid.androidnfcp2pdemo;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Priyadarshi Gangopadhyay on 13/09/2016.
 */

public class MainActivity extends AppCompatActivity {

    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private IntentFilter[] intentFilters;

    //DEFINITING THE TYPE OF DATA TO BE TRANSFERRED VIA NFC
    private final String MIME_TYPE = "text/plain";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initNfc();
    }

    private void initNfc() {

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {

            //NO NFC ON DEVICE
            Toast.makeText(this, "This device does not support NFC.", Toast.LENGTH_LONG).show();
            return;
        }

        if (nfcAdapter.isEnabled()) {

            nfcAdapter.setOnNdefPushCompleteCallback(new NfcAdapter.OnNdefPushCompleteCallback() {

                @Override
                public void onNdefPushComplete(NfcEvent event) {

                    //CAN DO SOMETHING AFTER COMPLETION OF ANDROID BEAM
                    //WRITE CODE FOR THE SAME HERE
                }
            }, this);

            pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
            IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
            try {

                ndefDetected.addDataType(MIME_TYPE);
            } catch (IntentFilter.MalformedMimeTypeException e) {

                Log.e(this.toString(), e.getMessage());
            }
            intentFilters = new IntentFilter[]{ndefDetected};
        } else {

            //NFC AVAILABLE BUT DISABLED
            Toast.makeText(this, "Please enable NFC", Toast.LENGTH_LONG).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

                Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
                startActivity(intent);
            } else {

                Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                startActivity(intent);
            }
        }
    }

    private void enableNdefExchangeMode(String messageSent) {

        //THIS FUNCTION IS USED TO SET OUTGOING MESSAGE
        //EXAMPLE, USE THE CALL enableNdefExchangeMode("HelloWorld") TO SET OUTGOING MESSAGE TO 'HelloWorld'
        //ALTERNATIVELY USE USER INPUT/HARD CODED STRING TO SET OUTGOING MESSAGE
        messageSent = "This is a hardcoded message";
        NdefMessage message = NFCUtils.getNewMessage(MIME_TYPE, messageSent.getBytes());
        nfcAdapter.setNdefPushMessage(message, this);
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
    }

    @Override
    protected void onNewIntent(Intent intent) {

        //THIS FUNCTION IS USED TO PARSE INCOMING MESSAGES
        super.onNewIntent(intent);
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {

            List<String> messageReceived = NFCUtils.getStringsFromNfcIntent(intent);
            String incomingMessage = messageReceived.get(0);
            //PERFORM ACTION BASED ON incomingMessage STRING DATA
            Toast.makeText(this, incomingMessage, Toast.LENGTH_SHORT).show();
        }
    }
}

