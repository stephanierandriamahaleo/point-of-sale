package mg.nexthope.point_de_vente_app.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dantsu.escposprinter.EscPosPrinter;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections;
import com.dantsu.escposprinter.textparser.PrinterTextParserImg;
import com.mazenrashed.printooth.Printooth;
import com.mazenrashed.printooth.data.printable.ImagePrintable;
import com.mazenrashed.printooth.data.printable.Printable;
import com.mazenrashed.printooth.data.printer.DefaultPrinter;
import com.mazenrashed.printooth.ui.ScanningActivity;
import com.zxy.tiny.Tiny;
import com.zxy.tiny.callback.BitmapCallback;

import net.posprinter.posprinterface.IMyBinder;
import net.posprinter.posprinterface.ProcessData;
import net.posprinter.posprinterface.UiExecute;
import net.posprinter.service.PosprinterService;
import net.posprinter.utils.BitmapToByteData;
import net.posprinter.utils.DataForSendToPrinterPos80;
import net.posprinter.utils.PosPrinterDev;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import mg.nexthope.point_de_vente_app.R;
import mg.nexthope.point_de_vente_app.adapters.CheckoutSummaryListAdapter;
import mg.nexthope.point_de_vente_app.api.Api;
import mg.nexthope.point_de_vente_app.api.JsonPlaceholderApi;
import mg.nexthope.point_de_vente_app.constants.Constant;
import mg.nexthope.point_de_vente_app.constants.PrintConstant;
import mg.nexthope.point_de_vente_app.models.CheckoutSummary;
import mg.nexthope.point_de_vente_app.models.TicketRequest;
import mg.nexthope.point_de_vente_app.utils.DeviceReceiver;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.Okio;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class CheckoutSummaryActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private TextView checkoutNumber;
    private TextView checkoutDate;
    private TextView purchaserName;
    private TextView amount;
    private TextView currencyTextView;

    private ImageButton finishButton;
    private ImageButton printButton;
    private View printButtonLayout;

    private RecyclerView recyclerView;
    private LinearLayoutManager manager;
    private CheckoutSummaryListAdapter checkoutSummaryListAdapter;

    private int shopId;
    private int eventId;
    private String currency;
    private CheckoutSummary checkoutSummary;

    BluetoothAdapter bluetoothAdapter;
    private View dialogView;
    private ArrayAdapter<String> adapter1,adapter2,adapter3;;
    private ListView lv1,lv2,lv_usb;
    private ArrayList<String> deviceList_bonded=new ArrayList<String>();//bonded list
    private ArrayList<String> deviceList_found=new ArrayList<String>();//found list
    private Button btn_scan; //scan button
    private LinearLayout LLlayout;
    AlertDialog dialog;
    String mac;
    int pos ;
    String showET = "";
    public static IMyBinder binder;
    //bindService connection
    ServiceConnection conn;

    private static DeviceReceiver myDevice;

    public static boolean ISCONNECT;

    View dialogView3;
    private TextView tv_usb;
    private List<String> usbList,usblist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_summary);

        ServiceConnection conn= new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                //Bind successfully
                binder = (IMyBinder) iBinder;
                Log.e("binder","connected");
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                Log.e("disbinder","disconnected");
            }
        };

        Intent intent=new Intent(this, PosprinterService.class);
        bindService(intent, conn, BIND_AUTO_CREATE);

        // Printooth.INSTANCE.init(CheckoutSummaryActivity.this);

        eventId = getIntent().getExtras().getInt("eventId");
        shopId = getIntent().getExtras().getInt("shopId");
        currency = getIntent().getExtras().getString("currency");
        checkoutSummary = (CheckoutSummary) getIntent().getExtras().get("checkoutSummary");

        initToolbar();
        initComponent();
    }

    private void initComponent() {
        sharedPreferences = getSharedPreferences(Constant.PREFERENCES, Context.MODE_PRIVATE);
        checkoutNumber = findViewById(R.id.checkout_summary_checkout_number);
        checkoutDate = findViewById(R.id.checkout_summary_reservation_date);
        purchaserName = findViewById(R.id.checkout_summary_purchaser);
        amount = findViewById(R.id.checkout_summary_amount);
        currencyTextView = findViewById(R.id.checkout_summary_currency);
        printButtonLayout = findViewById(R.id.print_btn_layout);


        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        try {
            Date date = dateFormat.parse(checkoutSummary.getReservationDate());
            dateFormat.applyPattern("dd/MM/yyyy à HH:mm");
            checkoutDate.setText(dateFormat.format(date));

        } catch (ParseException e) {
            e.printStackTrace();
        }

        checkoutNumber.setText(checkoutSummary.getCheckoutNumber());
        purchaserName.setText(checkoutSummary.getPurchaser());
        amount.setText(checkoutSummary.getAmount() + "");
        currencyTextView.setText(currency);

        finishButton = findViewById(R.id.finish);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS),
                        "test.pdf"
                );
                if (file.exists()) {
                    file.delete();
                }

                Intent intent = new Intent(CheckoutSummaryActivity.this, TicketListActivity.class);
                intent.putExtra("shopId", shopId);
                intent.putExtra("eventId", eventId);
                startActivity(intent);

            }
        });

        printButton = findViewById(R.id.print);
        printButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                printTicket();
            }
        });


        checkoutSummaryListAdapter = new CheckoutSummaryListAdapter(CheckoutSummaryActivity.this, checkoutSummary.getTickets());
        recyclerView = findViewById(R.id.checkout_summary_recycler_view);
        manager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(checkoutSummaryListAdapter);
    }

    /*@RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ScanningActivity.SCANNING_FOR_PRINTER && resultCode == Activity.RESULT_OK) {

            try {
                File file = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS),
                        "test.pdf"
                );

                PdfRenderer renderer = new PdfRenderer(ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY));
                final int pageCount = renderer.getPageCount();
                ArrayList<Printable> bitmaps = new ArrayList<>();
                for (int i = 0; i < pageCount; i++) {
                    PdfRenderer.Page page = renderer.openPage(i);
                    int width = page.getWidth() + 351; // 165
                    int height = page.getHeight() + 600; // 300
                    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_PRINT);
                    double a = height / 255d;
                    Bitmap[] splittedBitmap = splitBitmap(bitmap, width, (int) Math.ceil(a));

                    for(int j = 0; j < splittedBitmap.length; j++ ) {
                        Printable printable = new ImagePrintable.Builder(splittedBitmap[j])
                                .build();
                        bitmaps.add(printable);
                    }
                    page.close();
                }
                Printooth.INSTANCE.printer().print(bitmaps);
                renderer.close();

                //renderer.close();


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }*/

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /* getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true); */
        getSupportActionBar().setTitle("Résumé de la commande");
    }

    public void printTicket() {
        /*ProgressDialog progressDialog = new ProgressDialog(CheckoutSummaryActivity.this);
        progressDialog.setTitle("Impression de vos billets");
        progressDialog.setMessage("Un instant...");
        progressDialog.setCancelable(false);
        progressDialog.show();*/
        JsonPlaceholderApi jsonPlaceHolderApi = Api.getApi(Constant.URL).create(JsonPlaceholderApi.class);
        TicketRequest ticketRequest = new TicketRequest(checkoutSummary.getReservationId(), shopId, eventId);
        Call<ResponseBody> printTicketCall = jsonPlaceHolderApi.printTicket(
                ticketRequest,
                sharedPreferences.getString("token", "")
        );

        printTicketCall.enqueue(new Callback<ResponseBody>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.code() == 200) {
                    byte[] body = null;

                    try {
                        File downloadedFile = new File(
                                Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS),
                                "test.pdf"
                        );
                        //downloadedFile.mkdirs();
                        if (!downloadedFile.exists()) {
                            downloadedFile.getParentFile().mkdirs();

                            downloadedFile.createNewFile();
                        }

                        BufferedSink sink = Okio.buffer(Okio.sink(downloadedFile));
                        sink.writeAll(response.body().source());
                        sink.close();
                        /* if (ISCONNECT) {
                            launchPrint();
                        } else {
                            setUSB();
                        } */
                        bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();

                        if (ISCONNECT && myDevice != null) {
                            launchPrint();
                        } else {
                            setBluetooth();
                        }


                        // progressDialog.dismiss();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(CheckoutSummaryActivity.this, "Une erreur est survenue", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    public Bitmap[] splitBitmap(Bitmap bitmap, int width, int yCount) {
        Bitmap[]bitmaps = new Bitmap[yCount];
        int height;

        height = bitmap.getHeight() / yCount;
        int x = 0;
        for(int y = 0; y < yCount; ++y) {
            bitmaps[y] = Bitmap.createBitmap(bitmap, 0, y * height, width, height);
        }
        return bitmaps;
    }

    /*
     select bluetooth device
     */

    public void setBluetooth(){
        bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();

        if (!bluetoothAdapter.isEnabled()){
            //open bluetooth
            Intent intent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, PrintConstant.ENABLE_BLUETOOTH);
        }else {
            showblueboothlist();
        }
    }

    private void showblueboothlist() {
        if (!bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.startDiscovery();
        }
        LayoutInflater inflater=LayoutInflater.from(this);
        dialogView=inflater.inflate(R.layout.printer_list, null);
        adapter1=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, deviceList_bonded);
        lv1=(ListView) dialogView.findViewById(R.id.listView1);
        btn_scan=(Button) dialogView.findViewById(R.id.btn_scan);
        LLlayout=(LinearLayout) dialogView.findViewById(R.id.ll1);
        lv2=(ListView) dialogView.findViewById(R.id.listView2);
        adapter2=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, deviceList_found);
        lv1.setAdapter(adapter1);
        lv2.setAdapter(adapter2);
        dialog=new AlertDialog.Builder(this).setTitle("Imprimantes").setView(dialogView).create();
        dialog.show();

        myDevice=new DeviceReceiver(deviceList_found,adapter2,lv2);

        //register the receiver
        IntentFilter filterStart=new IntentFilter(BluetoothDevice.ACTION_FOUND);
        IntentFilter filterEnd=new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(myDevice, filterStart);
        registerReceiver(myDevice, filterEnd);

        setDlistener();
        findAvalaibleDevice();
    }

    private void setDlistener() {
        // TODO Auto-generated method stub
        btn_scan.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                LLlayout.setVisibility(View.VISIBLE);
                //btn_scan.setVisibility(View.GONE);
            }
        });
        //boned device connect
        lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                try {
                    if(bluetoothAdapter!=null&&bluetoothAdapter.isDiscovering()){
                        bluetoothAdapter.cancelDiscovery();

                    }

                    String msg=deviceList_bonded.get(arg2);
                    mac=msg.substring(msg.length()-17);
                    String name=msg.substring(0, msg.length()-18);
                    //lv1.setSelection(arg2);
                    dialog.cancel();
                    showET = mac;
                    connetBle();

                    //Log.i("TAG", "mac="+mac);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        //found device and connect device
        lv2.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                try {
                    if(bluetoothAdapter!=null&&bluetoothAdapter.isDiscovering()){
                        bluetoothAdapter.cancelDiscovery();

                    }
                    String msg=deviceList_found.get(arg2);
                    mac=msg.substring(msg.length()-17);
                    String name=msg.substring(0, msg.length()-18);
                    //lv2.setSelection(arg2);
                    dialog.cancel();
                    Log.i("TAG", "mac="+mac);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }

    private void findAvalaibleDevice() {
        // TODO Auto-generated method stub

        Set<BluetoothDevice> device=bluetoothAdapter.getBondedDevices();

        deviceList_bonded.clear();
        if(bluetoothAdapter!=null&&bluetoothAdapter.isDiscovering()){
            adapter1.notifyDataSetChanged();
        }
        if(device.size()>0){
            //already
            for(Iterator<BluetoothDevice> it = device.iterator(); it.hasNext();){
                BluetoothDevice btd=it.next();
                deviceList_bonded.add(btd.getName()+'\n'+btd.getAddress());
                adapter1.notifyDataSetChanged();
            }
        }else{
            deviceList_bonded.add("No can be matched to use bluetooth");
            adapter1.notifyDataSetChanged();
        }

    }

    private void connetBle(){
        String bleAdrress=showET;
        if (bleAdrress.equals(null)||bleAdrress.equals("")){
            Toast.makeText(CheckoutSummaryActivity.this, "Veuillez choisir une imprimante", Toast.LENGTH_LONG);
        }else {
            binder.connectBtPort(bleAdrress, new UiExecute() {
                @Override
                public void onsucess() {
                    Toast.makeText(CheckoutSummaryActivity.this, "Connexion réussie", Toast.LENGTH_LONG).show();
                    ISCONNECT=true;

                    binder.write(DataForSendToPrinterPos80.openOrCloseAutoReturnPrintState(0x1f), new UiExecute() {
                        @Override
                        public void onsucess() {
                            launchPrint();
                        }

                        @Override
                        public void onfailed() {
                            Toast.makeText(CheckoutSummaryActivity.this, "Connexion impossible", Toast.LENGTH_LONG).show();
                        }
                    });


                }

                @Override
                public void onfailed() {
                    // ISCONNECT=false;
                }
            });
        }
    }

    private void launchPrint() {
        ProgressDialog progressDialog = new ProgressDialog(CheckoutSummaryActivity.this);
        progressDialog.setTitle("Impression de vos billets");
        progressDialog.setMessage("Un instant...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        File file = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS),
                "test.pdf"
        );

        PdfRenderer renderer = null;
        try {
            renderer = new PdfRenderer(ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY));
        } catch (IOException e) {
            e.printStackTrace();
        }
        final int pageCount = renderer.getPageCount();
        ArrayList<Printable> bitmaps = new ArrayList<>();
        for (int i = 0; i < pageCount; i++) {
            PdfRenderer.Page page = renderer.openPage(i);
            int width = page.getWidth() + 351; // 165
            int height = page.getHeight() + 600; // 300
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_PRINT);
            if (PosPrinterDev.PortType.USB!=CheckoutSummaryActivity.portType){
                printpicCode(bitmap);
            }else {
                printUSBbitmap(bitmap);

            }
            page.close();
        }
        renderer.close();
        progressDialog.dismiss();
    }

    private void printUSBbitmap(final Bitmap printBmp){

        int height=printBmp.getHeight();
        // if height > 200 cut the bitmap
        if (height>200){

            binder.writeDataByYouself(new UiExecute() {
                @Override
                public void onsucess() {

                }

                @Override
                public void onfailed() {

                }
            }, new ProcessData() {
                @Override
                public List<byte[]> processDataBeforeSend() {
                    List<byte[]> list=new ArrayList<byte[]>();
                    list.add(DataForSendToPrinterPos80.initializePrinter());
                    List<Bitmap> bitmaplist=new ArrayList<>();
                    bitmaplist=cutBitmap(200,printBmp);//cut bitmap
                    if(bitmaplist.size()!=0){
                        for (int i=0;i<bitmaplist.size();i++){
                            list.add(DataForSendToPrinterPos80.printRasterBmp(0,bitmaplist.get(i),BitmapToByteData.BmpType.Threshold,BitmapToByteData.AlignType.Center,576));
                        }
                    }
                    list.add(DataForSendToPrinterPos80.selectCutPagerModerAndCutPager(66,1));
                    return list;
                }
            });
        }else {
            binder.writeDataByYouself(new UiExecute() {
                @Override
                public void onsucess() {

                }

                @Override
                public void onfailed() {

                }
            }, new ProcessData() {
                @Override
                public List<byte[]> processDataBeforeSend() {
                    List<byte[]> list=new ArrayList<byte[]>();
                    list.add(DataForSendToPrinterPos80.initializePrinter());
                    list.add(DataForSendToPrinterPos80.printRasterBmp(
                            0,printBmp, BitmapToByteData.BmpType.Threshold, BitmapToByteData.AlignType.Center,576));
                    list.add(DataForSendToPrinterPos80.selectCutPagerModerAndCutPager(66,1));
                    return list;
                }
            });
        }

    }

    private List<Bitmap> cutBitmap(int h,Bitmap bitmap){
        int width=bitmap.getWidth();
        int height=bitmap.getHeight();
        boolean full=height%h==0;
        int n=height%h==0?height/h:(height/h)+1;
        Bitmap b;
        List<Bitmap> bitmaps=new ArrayList<>();
        for (int i=0;i<n;i++){
            if (full){
                b=Bitmap.createBitmap(bitmap,0,i*h,width,h);
            }else {
                if (i==n-1){
                    b=Bitmap.createBitmap(bitmap,0,i*h,width,height-i*h);
                }else {
                    b=Bitmap.createBitmap(bitmap,0,i*h,width,h);
                }
            }

            bitmaps.add(b);
        }

        return bitmaps;
    }

    private void printpicCode(final Bitmap printBmp){
        Tiny.BitmapCompressOptions options = new Tiny.BitmapCompressOptions();
        Tiny.getInstance().source(printBmp).asBitmap().withOptions(options).compress(new BitmapCallback() {
            @Override
            public void callback(boolean isSuccess, Bitmap bitmap) {
                if (isSuccess){
                    binder.writeDataByYouself(new UiExecute() {
                        @Override
                        public void onsucess() {
                            Toast.makeText(CheckoutSummaryActivity.this, "Impression terminée", Toast.LENGTH_LONG).show();
                            //printButtonLayout.setVisibility(View.GONE);

                        }

                        @Override
                        public void onfailed() {
                            Toast.makeText(CheckoutSummaryActivity.this, "Erreur lors de l'impression", Toast.LENGTH_LONG).show();
                            setBluetooth();
                        }
                    }, new ProcessData() {
                        @Override
                        public List<byte[]> processDataBeforeSend() {
                            List<byte[]> list=new ArrayList<byte[]>();
                            list.add(DataForSendToPrinterPos80.initializePrinter());
                            list.add(DataForSendToPrinterPos80.printRasterBmp(
                                    0,bitmap, BitmapToByteData.BmpType.Threshold, BitmapToByteData.AlignType.Left,576));
//                list.add(DataForSendToPrinterPos80.printAndFeedForward(3));
                            list.add(DataForSendToPrinterPos80.selectCutPagerModerAndCutPager(66,1));
                            return list;
                        }
                    });
                }
            }
        });

    }

    /*
   uSB connection
    */
    private void setUSB(){
        LayoutInflater inflater=LayoutInflater.from(this);
        dialogView3=inflater.inflate(R.layout.usb_link,null);
        tv_usb= (TextView) dialogView3.findViewById(R.id.textView1);
        lv_usb= (ListView) dialogView3.findViewById(R.id.listView1);


        usbList= PosPrinterDev.GetUsbPathNames(this);
        if (usbList==null){
            usbList=new ArrayList<>();
        }
        usblist=usbList;
        adapter3=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,usbList);
        lv_usb.setAdapter(adapter3);


        AlertDialog dialog=new AlertDialog.Builder(this)
                .setView(dialogView3).create();
        dialog.show();

        setUsbListener(dialog);

    }
    String usbDev="";
    public void setUsbListener(final AlertDialog dialog) {

        lv_usb.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                usbDev=usbList.get(i);
                mac = usbDev;
                dialog.cancel();

                connectUSB();
            }
        });
    }

    private void connectUSB() {
        if (mac == null || mac.isEmpty()||mac.compareTo("") == 0){
            Toast.makeText(CheckoutSummaryActivity.this, "Veuillez choisir une imprimante", Toast.LENGTH_LONG);
        } else {
            binder.connectUsbPort(getApplicationContext(), mac, new UiExecute() {
                @Override
                public void onsucess() {
                    ISCONNECT=true;
                    Toast.makeText(CheckoutSummaryActivity.this, "Connexion réussie", Toast.LENGTH_LONG).show();

                    binder.write(DataForSendToPrinterPos80.openOrCloseAutoReturnPrintState(0x1f), new UiExecute() {
                        @Override
                        public void onsucess() {
                            launchPrint();
                        }

                        @Override
                        public void onfailed() {
                            Toast.makeText(CheckoutSummaryActivity.this, "Connexion impossible", Toast.LENGTH_LONG).show();
                        }
                    });
                    setPortType(PosPrinterDev.PortType.USB);
                }

                @Override
                public void onfailed() {
                                    }
            });
        }
    }

    @Override
    public void onBackPressed() {

    }

    public static PosPrinterDev.PortType portType;//connect type
    private void setPortType(PosPrinterDev.PortType portType){
        this.portType=portType;

    }
}