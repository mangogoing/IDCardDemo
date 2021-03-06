package com.qwm.idcarddemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.qwm.idcarddemo.bean.IdCardBean;
import com.qwm.idcarddemo.utils.IDCardReadUtils;
import com.qwm.idcarddemo.view.OneColumDialog;
import com.synjones.bluetooth.DecodeWlt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import android_serialport_api.SerialPortFinder;

/**
 * @author qiwenming
 * @date 2016/1/20 0020 下午 1:32
 * @ClassName: MainActivity
 * @ProjectName:
 * @PackageName: com.qwm.idcarddemo
 * @Description: 身份证demo
 */
public class MainActivity extends AppCompatActivity {
    private static Process localProcess1;
    private static String str;
    private static BufferedReader localBufferedReader;
    private static StringBuilder localStringBuilder;
    /**
     * 设备的地址
     */
    private TextView addressTv;
    /**
     * 设备的波特率
     */
    private TextView bauteRateTv;
    /**
     * 读取到身份证信息
     */
    private TextView infoTv;
    /**
     * 身份证上的头像
     */
    private ImageView headIv;
    /**
     * 串口
     */
    public SerialPortFinder mSerialPortFinder = new SerialPortFinder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addressTv = (TextView) findViewById(R.id.tv_devices_address);
        bauteRateTv = (TextView) findViewById(R.id.tv_baute_rate);
        infoTv = (TextView) findViewById(R.id.tv_info);
        headIv = (ImageView) findViewById(R.id.iv_head);
//        headIv.setImageBitmap(headXm());
    }

    /**
     * 获取全部窗口地址
     *
     * @return
     */
    public List<String> getAllDevicesPath() {
        return Arrays.asList(mSerialPortFinder.getAllDevicesPath());
    }

    /**
     * 获取全部 波特率
     *
     * @return
     */
    public List<String> getAllBautRate() {
        return Arrays.asList(getResources().getStringArray(R.array.baudrates));
    }


    /**
     * 选择波特率
     *
     * @param view
     */
    public void selectBauteRate(View view) {
        OneColumDialog dialog = new OneColumDialog(this, getAllBautRate(), new OneColumDialog.SelectListener() {
            @Override
            public void selected(int position, String value) {
                bauteRateTv.setText(value);
            }
        });
        dialog.show();
    }

    /**
     * 选择设备地址
     *
     * @param view
     */
    public void selectAddress(View view) {
        List<String> list = getAllDevicesPath();
        if (list == null || list.size() <= 0) {
            Toast.makeText(this, "木有串口设备哦", Toast.LENGTH_SHORT).show();
            return;
        }
        OneColumDialog dialog = new OneColumDialog(this, list, new OneColumDialog.SelectListener() {
            @Override
            public void selected(int position, String value) {
                addressTv.setText(value);
            }
        });
        dialog.show();
    }


    public void readIdCard1(View view) {
        xmxxxx();
    }
    /**
     * 读取 身份证信息
     *
     * @param view
     */
    public void readIdCard(View view) {
        //1.硬件地址判断
        String adress = addressTv.getText().toString().trim();
        if ("".equals(adress)) {
            Toast.makeText(this, "请选择硬件地址", Toast.LENGTH_SHORT).show();
            return;
        }
        //2.波特率判断
        String bauteStr = bauteRateTv.getText().toString().trim();
        if ("".equals(bauteStr)) {
            Toast.makeText(this, "请选择波特率", Toast.LENGTH_SHORT).show();
            return;
        }
        new IDCardReadUtils(this).queryIdCardInfo(adress, Integer.parseInt(bauteStr), new IDCardReadUtils.IDCardListener() {
            @Override
            public void onInfo(IdCardBean bean) {
                //输出身份证信息
                infoTv.setText(bean.word.toMyString());
                decodeImagexxx(bean.headImage);
                //头像的处理，先不处理
//                headIv.setImageBitmap(bytes2Bimap(bean.headImage));
//                try {
//                    if (bean.headImage != null) {
//                        byte[] bbbb = decode(bean.headImage);
////                        Bitmap bmp = BitmapFactory.decodeByteArray(bbbb, 0, bbbb.length);
////                        headIv.setImageBitmap(bmp);
//                    }
//                } catch (Exception e) {
//
//                }


            }
        });
    }

   public String bmpPath =   "/sdcard/photo.bmp";
    public String wltPath =  "/sdcard/photo.wlt";

    public void decodeImagexxx(byte[] wlt) {
        if (wlt == null) {
            return;
        }
        try {
            File wltFile = new File(wltPath);
            FileOutputStream fos = new FileOutputStream(wltFile);
            fos.write(wlt);
            fos.close();

            DecodeWlt dw = new DecodeWlt();
            int result = dw.Wlt2Bmp(wltPath, bmpPath);

            if (result == 1) {
                File f = new File(bmpPath);
                if (f.exists())
                    headIv.setImageBitmap(BitmapFactory
                            .decodeFile(bmpPath));
                else {
//                    imageViewPhoto.setImageResource(R.drawable.photo);
                }
            } else {
//                imageViewPhoto.setImageResource(R.drawable.photo);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }


    /**
     * 将加密的照片byte数据通过jni解析
     *
     * @param wlt 解密前
     * @return 解密后
     * @throws RemoteException 解密错误
     */
    public static byte[] decode(byte[] wlt) throws RemoteException {
//        String bmpPath = "/data/data/com.cjy.filing/files/photo.bmp";
//        String wltPath = "/data/data/com.cjy.filing/files/photo.wlt";

//        String bmpPath = Environment.getExternalStorageDirectory().getPath() + "/photo.bmp";
//        String wltPath = Environment.getExternalStorageDirectory().getPath() + "/photo.wlt";
        String bmpPath =   "/sdcard/photo.bmp";
        String wltPath =  "/sdcard/photo.wlt";

        Log.i("bmpPath------------",bmpPath);

        File wltFile = new File(wltPath);
        File oldBmpPath = new File(bmpPath);
        if (oldBmpPath.exists() && oldBmpPath.isFile()) {
            oldBmpPath.delete();
        }


        try {
            FileOutputStream fos = new FileOutputStream(wltFile);
            fos.write(wlt);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        DecodeWlt dw = new DecodeWlt();

        int result = dw.Wlt2Bmp(wltPath, bmpPath);
        byte[] buffer = null;
        FileInputStream fin;
        try {
            File bmpFile = new File(bmpPath);
            fin = new FileInputStream(bmpFile);
            int length = fin.available();
            buffer = new byte[length];
            fin.read(buffer);
            fin.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return buffer;
    }




    public void xmxxxx(){
        String bmpPath =   "/sdcard/photo.bmp";
        String wltPath =  "/sdcard/photo.wlt";
        DecodeWlt dw = new DecodeWlt();

        int result = dw.Wlt2Bmp(wltPath, bmpPath);
        byte[] buffer = null;
        FileInputStream fin;
        try {
            File bmpFile = new File(bmpPath);
            fin = new FileInputStream(bmpFile);
            int length = fin.available();
            buffer = new byte[length];
            fin.read(buffer);
            fin.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if(buffer!=null){
            Bitmap bmp = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
                        headIv.setImageBitmap(bmp);
        }
    }





    /**
     * 将加密的照片byte数据通过jni解析
     *
     * @param wlt 解密前
     * @return 解密后
     * @throws RemoteException 解密错误
     */
    public static byte[] decode1(byte[] wlt) throws RemoteException {
//        String bmpPath = "/data/data/com.cjy.filing/files/photo.bmp";
//        String wltPath = "/data/data/com.cjy.filing/files/photo.wlt";

        String bmpPath = Environment.getExternalStorageDirectory().getPath() + "/photo.bmp";
        String wltPath = Environment.getExternalStorageDirectory().getPath() + "/photo.wlt";

        File wltFile = new File(wltPath);
        File oldBmpPath = new File(bmpPath);
        if (oldBmpPath.exists() && oldBmpPath.isFile()) {
            oldBmpPath.delete();
        }


        try {
            FileOutputStream fos = new FileOutputStream(wltFile);
            fos.write(wlt);
            fos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        DecodeWlt dw = new DecodeWlt();

        int result = dw.Wlt2Bmp(wltPath, bmpPath);
        byte[] buffer = null;
        FileInputStream fin;
        try {
            File bmpFile = new File(bmpPath);
            fin = new FileInputStream(bmpFile);
            int length = fin.available();
            buffer = new byte[length];
            fin.read(buffer);
            fin.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return buffer;
    }








    private Bitmap headXm(){
        String xxx = "574c66007e00320000ff851e5151513e710dd564f3d4095724f3ddf895443cfc59639173e02bfd562ab0078aee612fc793653e8c03f3ce9f348010127b23d5abb0a24bbde4c4ef4b8eb622c86687c2a0a0dd55d5dba16c137de1ce6951aed55251515a3e8160b84f172ba67d1ca405f39b09d6a3cf2892e87c28a696ece08f6cb37ee4d93916c66970b8d78db0165b2558ba157d44f34bed4d02382f4810f1f8a61588abeefe4259ccb2a37299bac8813ec3d57e2cedfefaf015301ecdafc4e2aa3129788416c1114abae5c5037d0ae71cb671af176d72b3f7ab8fb2d8d34f1e9ccc973d759f2c6cfbc706b405c7a28ebc0b73972584958d447cbe2aa67318879fac3094688ae8a22706477117a7975129fa17d6a9a3d42245312f1a0876752a5efac95c5877209626ba443bc424f2e827c190bb30b95db5525358ddfa16ffe9adb2e0de5b90bd47a1856fd8f3d0e38ed220170217e2ae5168873722f674e01bafb21b74b44d3232386740dc5c6829e2cd1d46118dbc443d00ec60d99226121f815339327e4617014b98bf5509554144c635b27e90fb8bc3ae51c5bfc7db37987c901e0de044f0b5e51d4d7742d5ec60d80caa7641797fedcb78d73e09d529d5906a6f4c2a2740df30426df9a8ececd4828c9fd70489b3f3f206bd8161ccc5518d55da71a24a2b7aff2ad474565eec35d7dd1561b550b5bbf2de58261650417efcc0fec1a02f53737e92f66e73c09121048d922f982bb480b986c032e23a3a0ef75ac9f00ea903f0fa978ef9313a3ba7a615c2df4e32edb7e0656aae515c1fefdd8269c4a7c1c37d2b0578f6084a2a8194ea369d3cf1bbe418765b410300327d0516ddc2792fed098922286095873a28a4a1fbd704a2c2369b1ca6126e18cde598d0c9e9167f1d302aea0df69fbb578d34f423f86bf4447b96dcdd7b6a5666881cd8b33e719ba56ea111d84e148ede9702fbc8856285f7907d22d6d3c097ca7c576f57608d34e7c8117ee10ce7d558d2cf98479b30ae514a1e88a4dae9e19346ca976a0248a3774bc30c24bb5ea025b5c87cde62153922e61e914fa4f9c8604235452f4b4366ae51157346215d3cb5075f04e937962fabc6af9ebd91e98832d88daf47d7d29361edadc93ce84b7313a5efc484ed737c45d9d8c59f88388fb28b2b9289057e344b0b08fdfa60ffcb761583115b632df7b037d80dfca568636843ac9b2c02905a3ef2462db40fbb338bae51dea81011adb54ea28a0179f42deefd1831e85a177f4b3f27e929c1795da7851ef5359b679b67b7b2d325abdf4bcfb43f627c3f7655fd4ca7a34d27214b2c00ed42ffb0f63a5a3e84b4a0a328829152fb7ea842591d364a055ff4f890ce4f1a775e96a62d21ba068f7ae162ed2a55edbdd289512c1199e2172ae0d1c8a66bf0efb68f563253b611db3f1761344ed1de";
//        String ttt = "NTc0YzY2MDA3ZTAwMzIwMDAwZmY4NTFlNTE1MTUxM2U3MTBkZDU2NGYzZDQwOTU3MjRmM2RkZjg5NTQ0M2NmYzU5NjM5MTczZTAyYmZkNTYyYWIwMDc4YWVlNjEyZmM3OTM2NTNlOGMwM2YzY2U5ZjM0ODAxMDEyN2IyM2Q1YWJiMGEyNGJiZGU0YzRlZjRiOGViNjIyYzg2Njg3YzJhMGEwZGQ1NWQ1ZGJhMTZjMTM3ZGUxY2U2OTUxYWVkNTUyNTE1MTVhM2U4MTYwYjg0ZjE3MmJhNjdkMWNhNDA1ZjM5YjA5ZDZhM2NmMjg5MmU4N2MyOGE2OTZlY2UwOGY2Y2IzN2VlNGQ5MzkxNmM2Njk3MGI4ZDc4ZGIwMTY1YjI1NThiYTE1N2Q0NGYzNGJlZDRkMDIzODJmNDgxMGYxZjhhNjE1ODhhYmVlZmU0MjU5Y2NiMmEzNzI5OWJhYzg4MTNlYzNkNTdlMmNlZGZlZmFmMDE1MzAxZWNkYWZjNGUyYWEzMTI5Nzg4NDE2YzExMTRhYmFlNWM1MDM3ZDBhZTcxY2I2NzFhZjE3NmQ3MmIzZjdhYjhmYjJkOGQzNGYxZTljY2M5NzNkNzU5ZjJjNmNmYmM3MDZiNDA1YzdhMjhlYmMwYjczOTcyNTg0OTU4ZDQ0N2NiZTJhYTY3MzE4ODc5ZmFjMzA5NDY4OGFlOGEyMjcwNjQ3NzExN2E3OTc1MTI5ZmExN2Q2YTlhM2Q0MjI0NTMxMmYxYTA4NzY3NTJhNWVmYWM5NWM1ODc3MjA5NjI2YmE0NDNiYzQyNGYyZTgyN2MxOTBiYjMwYjk1ZGI1NTI1MzU4ZGRmYTE2ZmZlOWFkYjJlMGRlNWI5MGJkNDdhMTg1NmZkOGYzZDBlMzhlZDIyMDE3MDIxN2UyYWU1MTY4ODczNzIyZjY3NGUwMWJhZmIyMWI3NGI0NGQzMjMyMzg2NzQwZGM1YzY4MjllMmNkMWQ0NjExOGRiYzQ0M2QwMGVjNjBkOTkyMjYxMjFmODE1MzM5MzI3ZTQ2MTcwMTRiOThiZjU1MDk1NTQxNDRjNjM1YjI3ZTkwZmI4YmMzYWU1MWM1YmZjN2RiMzc5ODdjOTAxZTBkZTA0NGYwYjVlNTFkNGQ3NzQyZDVlYzYwZDgwY2FhNzY0MTc5N2ZlZGNiNzhkNzNlMDlkNTI5ZDU5MDZhNmY0YzJhMjc0MGRmMzA0MjZkZjlhOGVjZWNkNDgyOGM5ZmQ3MDQ4OWIzZjNmMjA2YmQ4MTYxY2NjNTUxOGQ1NWRhNzFhMjRhMmI3YWZmMmFkNDc0NTY1ZWVjMzVkN2RkMTU2MWI1NTBiNWJiZjJkZTU4MjYxNjUwNDE3ZWZjYzBmZWMxYTAyZjUzNzM3ZTkyZjY2ZTczYzA5MTIxMDQ4ZDkyMmY5ODJiYjQ4MGI5ODZjMDMyZTIzYTNhMGVmNzVhYzlmMDBlYTkwM2YwZmE5NzhlZjkzMTNhM2JhN2E2MTVjMmRmNGUzMmVkYjdlMDY1NmFhZTUxNWMxZmVmZGQ4MjY5YzRhN2MxYzM3ZDJiMDU3OGY2MDg0YTJhODE5NGVhMzY5ZDNjZjFiYmU0MTg3NjViNDEwMzAwMzI3ZDA1MTZkZGMyNzkyZmVkMDk4OTIyMjg2MDk1ODczYTI4YTRhMWZiZDcwNGEyYzIzNjliMWNhNjEyNmUxOGNkZTU5OGQwYzllOTE2N2YxZDMwMmFlYTBkZjY5ZmJiNTc4ZDM0ZjQyM2Y4NmJmNDQ0N2I5NmRjZGQ3YjZhNTY2Njg4MWNkOGIzM2U3MTliYTU2ZWExMTFkODRlMTQ4ZWRlOTcwMmZiYzg4NTYyODVmNzkwN2QyMmQ2ZDNjMDk3Y2E3YzU3NmY1NzYwOGQzNGU3YzgxMTdlZTEwY2U3ZDU1OGQyY2Y5ODQ3OWIzMGFlNTE0YTFlODhhNGRhZTllMTkzNDZjYTk3NmEwMjQ4YTM3NzRiYzMwYzI0YmI1ZWEwMjViNWM4N2NkZTYyMTUzOTIyZTYxZTkxNGZhNGY5Yzg2MDQyMzU0NTJmNGI0MzY2YWU1MTE1NzM0NjIxNWQzY2I1MDc1ZjA0ZTkzNzk2MmZhYmM2YWY5ZWJkOTFlOTg4MzJkODhkYWY0N2Q3ZDI5MzYxZWRhZGM5M2NlODRiNzMxM2E1ZWZjNDg0ZWQ3MzdjNDVkOWQ4YzU5Zjg4Mzg4ZmIyOGIyYjkyODkwNTdlMzQ0YjBiMDhmZGZhNjBmZmNiNzYxNTgzMTE1YjYzMmRmN2IwMzdkODBkZmNhNTY4NjM2ODQzYWM5YjJjMDI5MDVhM2VmMjQ2MmRiNDBmYmIzMzhiYWU1MWRlYTgxMDExYWRiNTRlYTI4YTAxNzlmNDJkZWVmZDE4MzFlODVhMTc3ZjRiM2YyN2U5MjljMTc5NWRhNzg1MWVmNTM1OWI2NzliNjdiN2IyZDMyNWFiZGY0YmNmYjQzZjYyN2MzZjc2NTVmZDRjYTdhMzRkMjcyMTRiMmMwMGVkNDJmZmIwZjYzYTVhM2U4NGI0YTBhMzI4ODI5MTUyZmI3ZWE4NDI1OTFkMzY0YTA1NWZmNGY4OTBjZTRmMWE3NzVlOTZhNjJkMjFiYTA2OGY3YWUxNjJlZDJhNTVlZGJkZDI4OTUxMmMxMTk5ZTIxNzJhZTBkMWM4YTY2YmYwZWZiNjhmNTYzMjUzYjYxMWRiM2YxNzYxMzQ0ZWQxZGU=";
//        String ttt1 = "ffd8ffe000104a46494600010100000100010000ffdb004300080606070605080707070909080a0c140d0c0b0b0c1912130f141d1a1f1e1d1a1c1c20242e2720222c231c1c2837292c30313434341f27393d38323c2e333432ffdb0043010909090c0b0c180d0d1832211c213232323232323232323232323232323232323232323232323232323232323232323232323232323232323232323232323232ffc000110800a000a003012200021101031101ffc4001c0000000701010000000000000000000000000103040506070208ffc40045100001030302030603070203020f00000001020304000511062112314107132251617114328123425291a1b1c1336208157217d12543535455637382849294a2b2c2f1ffc400190100020301000000000000000000000000010300020405ffc400221100020202030003010101000000000000000102110321041231224151133242ffda000c03010002110311003f00c9225cd05410f0e127ef74a9d6ce5231b8f3154e6c65605493329e8ffd359c791e55b650bf0ac675e96715d64019276150ec5e80187918fee4d2936e4caa1a830e654adb18dc527a3b1bdd511735f3225b8ee36ce07b5776e67be9ed8e893c47e94d7aed5316568250e3c47cdb0f6a74be31131db1fca84ccb1e3041f349a87936a7980569505a073e86a74af03d6a3eeaff00045e007759c7d295093ba1b28aab20a8e850fad69100343950f5a140803ca850fad0a240b346373408f3a1eb4080e7458e543951d4203a536909c2c1f314e6927d3948344022c8cb9ec29d1de9bb1f39f3c539a84050e58a15db2cbb26434c30da9c79e586db40e6a51380283d0684c9c0c9d87214e999efb284a1252529e408a75a95a660dc19b230a4ac5bc70c8753ff0018f9dd7f407c23daa2f96f4135241f19269bbaf1e36813e869a4c9264b8158e1481b026b9830e55d26a21c08ceca90b380db49e23f5f2ab55f343af49e96371bfb894dc251eea1c16959e13d56b23c874aa39422ebecb7c9a2a14395709504349e238daa5ac3a6af7a99eeeed16f75f483853caf0b69f751daaf2928ab65526fc22fd68db4a9e7036d254eb87921b49513f415b569eec3e2b7c0f6a09eb94bff009bc5f0363dd5ccd6976bd3f6ab1b41bb6dba34548db2db638beaae758f27360b51d8f8f1dbf4f304ad2b7d81675dda6db97121a480172541b2b3d0252773f4a866d7c69cf0906bd03ad7b36ba6b5be89326fc8660b40263c64b4545b1d4f3c64f9d45c3ec1ed8850f8bbe4d793f85a6d28fd73461cc855c812c12bd18afbd720827620e07439af47c1ec8347c3c715b9d94a1d643c55fb6290d6dd98db6e5a61c4592df1e24f8c0b8c7729c7798e68579e7a7ad45cd839511e06959e79a15ca49390a052a070a07a11cebaad8201d28943228c73a1d68906f1fe634e39d378ff0031a7150082c8279ef575d0b1916c8177d632923b9b6325a89c4365c950c0c79e33fad532db0a45d2eecc28a8e37e4381b6c7a9ad6b5b69c99f07a7fb3dd3ad77a5b4195297c939e5c6b3e59c9fa0acf9f225511d8e2decc790b5baf296ae27643aa2700654b513bfeb5a5e93ec7aed792dcbbead56e84770c8feb2c7ff0051ea6b4dd17d9b5a74932878a1332e6478e5389cf09f240e83d6af096c7de20e7a563cbcc7fe603a1852dc8a458acef5be2a99d2502dd6f80951425f94952dc9246c5471be339e758cebebc5d35aeb44db988a1e7a1e6336cc5256952b3e2503e44d7a524c5cc0722475f71c4da9085a77e0c8e63f3a84d23a1ed5a46214436cb925dddf96eeee3a7dfa0f4a4e3cfd5f67e97942d57d142d17d8c478a1b9da9c26549e6986857d9a3fd47ef1f4e55ae4786d46610cb2d21a6923c2db690948f614e024018146693932cf23d968a51548203cb15cbaa6db696e38a09420152947900399aefceb971b43adadb7121685829524f506968256a25dafb74644db6daa1a602fc4cfc53e52ebc9e8a000c241e99a99b5dc5bba442ea5b534e2145b79973e669639a4d3c6db436da5b424250901294a46001e4293662b31dd90eb68095beb0b70fe256319a2da60d8b7d28bad1e6873f2a013cd7daf6973a77579b8b0df0c0b9e5d181b21cfbc9fe7eb5441cabd55aef4bb7abb4acab6e009291dec659fbae0e5f9f2af298438cbae477d05b79a514ad0a1b820e08aecf132f7853f518f2c699d0a3a20a0a1904518f3e55ac50d98f9ce0f4a70a38049e78a6ec36f1565b61d59c720d9346ebaa485216da90af25020feb55ec8893355ec2f4e7c75e65df5e465b883ba63fed14373f41fbd5fb5576a3a674bc97586ff00e10b8e385c6e281818e414bf4f2acb65eb51a6fb36b569cb0bbddcc98d2a44f9083e247113e11e4481f9566a79fafef58de07966e521ff00d7a2a46ab3fb79d44f288b7c1830d1d38925c57eb8a87576cbadc9c8b9b43d0474e2a847950c53971f1afa14f2499a0b3db56b56960aa6c67479391c62a72dddbfde5a5245c6d30e4a3ef16945b3fc8ac9586c38f042941209e66a495655291c4c3e959f2a12e3e37f4159247a1ec3db3694bc292d4879cb63eafbb287809f450daaf71e7c29880e46991de41dc16dd4abf9af1538d29a5a90b1850e62acba12df6bbb6a16ed7729f2e0095e161f8ebc00be8143d6b365e1c52b4c64733f0f5aa9d69b492b75b481d54b02abb7bd7fa634fb2a54dbbc752c726585778b3f4158ff0068ba22cfa2eca871dbe5d66ce92a288f1dc7309db9a95e82b25031bf2aa62e2466aecb4f2b8e8dc2edfe2080529167b2e53d1c96e633ff0074555e4f6e3ac5f2434b831c676eed8c91f99acdf14302b6478d8d7d097964cbeffb65d6ff00f4a35ffa74d3c89db8eb18ea1df2e1494f50b6304fd4566c00c8cf2a95447b63a0203c52a239938a2f063fc029c8da74ff006f56d94e219bedb97049d8bec1ef1b1ee398aabf6c1618c2746d5f6671b7edb72c25e7193c490e81cf6f31fa8acde75b8c440703816d93cfad4be95d4a2d45fb55c42a458678eee5c7fc19e4ea3c949e7f4aa2c0a12ed02ddfb2a641c73ba874e74b2d452826920943731d6da73bc6c294942ff10076346fac70800e77deb52762cf67a234768e511d947aa5b48fe2a32f964b1dc2d927fcd6db15f8e86d4b5953601d867208dc1a98e555cd7b24c3d017c7c1c11116011ea31fcd7031ca4e4b66f6951e495843afbab61052c9592da49c94a73b0cfb510655e5527061f1434288ce69c883e95da5349518bad9024628d2d950c8a72a63756dd69d428dde058c6e37ab7640ea469615e46bb6cbf1d5de34b5208f2a9af813e544a83e05784f2a1dd30f518b2a8ef32faa582a795f29a6292eb0b43ed928710a0a4a87420e41a958910ad93e1e471472e1f0437158e428765e13abf479ad35648d6d7a6673ada9b4b5190ca5b51ce081e23f53bd577ba382714eed8c77cb731d00daa41c8452d28f0f4a89a869129cb641a5b5286c36aebb957954b4487c71d2a033bd2ff007ca8f744ea40a9a5253922b8c6d5372e206e32891e82a38319076a2a560ea26da0a94da5c52bbacee3a62969f1986de022ab8938c9f7a91621154742b19c8a53e00e79557bab0f51ce89d0577d6d25d108b6c456084bd25d3e14e7a01cc9ad499ff000fd6a4a077f7d9aa5f5286920547f6052bbab8dfadc73c90e8f2d891feeadc7dab0723919233a43f1e38b56ce1695a91842f815d0e3354bed1a5b89d07788b2d9292e31c2dba81942ce46c7f09f7abaa95c292ac13819c0e66ab1a8ecf2af3a72e2273ce278a3a8b715a561292064711fbc7f4ac78dd49363a5e1815a20f15b1a38f3fde9fa6dfbf2a91d3d13bcb72918dd0e11f4201fe6a605bb07972ad72cd4c91c7a3317a3703eea08dd2a228a31319e0be1cf422ac7a86dc61dcc39c1e07c6463cfad453d1d411c613cb9d68592d0a78e8544f8d8dd95d3791382d052cb7c39db88d20403cb9d2e222b00a863d28da40516c736589dec6736c80bc7e94ade6206ad4e92304e123ea6a7748c12fdbe42c0dbbde107e95d5f6dcb9971856a6465c7160903cd4703f9348febf319fcfe254ad16d5c5b9ae3b8920ada0b19fcff6a9a7edc7e1dd38fb86ad9da0595163d576f9cda711d4c3685607e01c0afd3069755ab89a5a4273c49383e7b549e6b699218f546571252a2a780a02d07a1e94f15736ba4739f534826195a9481b14a8a4ede46917d95b0ac29271d08a7f64c5f56812e4aa5607004253c80f3a403384e48c7bd3b8d196faf3c3e01cc9a55d8ab2eb4c20712dd504a40f5a9dd2d1141bdb25edf6c26dcc123752734ebfcb0e7955bdbb5a1965a640f912135d18094a4ad430948249f41591e6d9a162d11dd8b472d6b0bda803c21ac7fef35b24bbadbe0ba96a54d61a755c9b52c711fa73ac4f41ca75999728e8966ded49085489a124a909c93c08c0d9473ccf215afd96d3628cc77f6b6a3bbc7f3490a0ead7eea3bd2f3d395b17155a2402d58eb40f8814ab74a860fb5452b50da5ac0766a5951e8ea14927f3149a750b12545bb6c6977074720d34529faa94001485190f72899bdb62a6d7a9665b9cd925d5323fd43c48fcd27f4ab02e28f2a6ba92c574ff003872e135965b765b617dcc5254a4f77d41eab4ecadba0229685715ce8c2315b28b814e5b5abfa6e8fc69fe473069935b1d869a22f515a5b9f64710a710d3cd9e3656ad805797d6a8716436ea4a16528790785682791ad94c19092eb72594940c049cf10706372474dea125681d3b394a5bb6f0dad47254caca4d5a1349548b4f15bed133b0c31c5c4108cf9d232e4478ed28ad692a2309483924f9568cc7651a6d4471a6691e5f106a6a0f67fa52c24ce4406c29af177d25c2be0fcf6ab7f58fe88926b544469cb4a6c3a45b7678eecf0990f67982790f7e95d68cb3cab9dea6df1494a5d642bb90e0f00788f083e8918cfa9a5e6b92b555d1a85050a4c749e34718e9ff002ab1d07e14f53bd6836db7316ab7b50a30c36d0c64f351e649f5277a576fb292f28a85dec37cbe693902f6988bb8c770bd1d1186c500614927d47f1513a51e4ce8222ad5991192373cd6dfdd57f07dab50dfa7ff00959eea5b23d63ba0bd5bb0db05654adbc2d28fcc143f02bf43bd0ed6a810d19aeaab68d39a99f0f0eee1cd3deb0e1f973f793ef51e1f8ca4e7be6c8f550adb99169d5500b1321b4e29072ec57920a9b3e63cc7911b5472fb33d2032a16363277d94aa62cb1ff00a2ead78638f5c61300f1486f6df00e73525a460ccb85f5372301cf876f665c7870a07f77a9c72f7ad39ad1ba7adeb0a8d6788850fbc519c7e75202229cf0b6129006013b25345e555511d1c57b911a191c5cb7f5a8fd40b11ad2b69240764fd927d07351fa27f7a937948b636e3f3240e15118427c5bf923a9cf95422195ddee0f4d9c3bb8715056f8cecd369f17779eab563c5e5ca9715b1b912ad165ecd2318ba7a5296d84a9d96a5631f7400123f2ab546b7c08b25c931e336cbae8fb428180af5206d9a8ed38da9bb1c652d3c2b7817d43c8ace71fad4a67ad56727664e88915b4db98ef1085e3971241aefa63a7a5151f2a759948fbc5b45ca1842161a90d2838c3b8f9163f83c8fbd5127d81b96ebdf0e96d89095e5f88e7c9c7f8938dd39f315a5f4dea2eed6745c52975a5fc3cd6c7d93e919c7a287de4fa546ac762c9d1efc33e4bf78b724b6e77fddf2e17925d4fd169df1ee29766fd83f691daf743d8ff00e42a67e39505f116f0d084f13e1773f60efaa57d0fa1a924b28580ae042d24641c020fd69525fa6e59135a2b875239f2c788d95f4e370af7f648dff3a34d9af57f750e4d5a9a61272953c80129f5437d4faaaad8cb696b742529ff0048029c874f5aa599e6db7a10b65ae2da63166320f88f138e28e56e2bcd47ad3deb49078677147de8cd4b17d581a79a7c28b4b4a825650ac1e4a1cc7bd21126c4ba4778b0b4bcd21c5b0e8239293b292735152ec8f2a5487edd78996e1215c4f36d252a4a958c150e21e127cc53eb5408b66b7b70a1a15dda4951538ae252d44e54a51ea49a2ea8aa52202e3a41c8ef26559d653c0729642f854dff00a15e5fda76a6e9bf5ce1fd95c230708eae20b4bfcc02926ae65d3e549adc246f823c8ef42ec6c6ca6bfa850a04a62807fb9e1feea64bbb4f920a23b69483d196d4e9fd702ac732ed6f8af16d482b706ea4b2c1594fbe06d5c3779b4bf1172113d84348385f1ab80a4f9149c1cfd2ac93342952d95f8f609729ef8896e2daf35a97c6f11e43a207b6f530cdad89ef35638ac81059525c9bc236e11b86c9eaa51dcfa7bd2b1bfcc6f8a09b630e45864f8a7c846323fead07727d4ed56bb7dbe3db21a62c5414a06e49395295d54a3d49a6a8d6d88cd9d55448d7f4fbad2caed53dc879dcb2b4871a3ec0f2fa521f05a9c6dded9d5ea50e0cfd33563da80a948caa5240a19a3e543144a85428e854209bccb521a2d3eda1d6d5cd0b0083508bd2505049b7bb2adca2727e19d213ff0094ed53f42a053688055aaf2ca7ec2e8cbf8e9258c67ea9c567779ed8a1e9fbc396d930da9cb6b671d80fe5295751b8e74ffb69d70e69eb2b765b7ba5bb8cf49e35a4e0b4d75c7a9e5f9d79acfd6b561e3466bb48a4b3ca3a47a199edcf4baf1dec6b8b47ae5b0afd8d4ed9bb4bb0dfdc71bb6377092e349e25a111b2a03cf19af2e6334bc49b2adf253261497a3be8f95c69652a1f514c970615a22e4cbecf572b574247cf06ea3ff02b34435647560b36abc3d9e5c30c8fdc8ac4adbdb5eac82d25b90a8b3c246389f6c851fa8e74f9eede750ad1c2d5b6dedabf11e257ee6b33e1cff072e444d7a56a3971a23b2d76392cc769256b7253c8692903cf9d65b71edee5f885bac8c2319016f3a55f5c0c567da875b6a0d50382e9705ad8072184781b07a6c2abe4784fb1ad18f8692b90a9f21b7f13d63614bec59212f3f6afb21d79407cca50c9348cd6e0c3d5768b9ca658056dbcdbeb7100f85290a0af707f7a93d20af8dd1b6590a4e14b86d9391e98a5ae5a798b9dd6dd31e74f750b8cf718ca5c2718cfb62b05f59335ca6a50a23de55cafea4bf2a54bb6c057f463c65f03aa1d14e2872cf448e54f74d5c26b7719763b8c83296c3697e3ca5ecb75a51230afee04119eb52af348c296b21290095289d80f3a85d2e9374bc4ed4294910dc6d31211231de36924a9cf65289c7a0a319390a9a8a8ebd2d94542851140a14742a102a3a145d2a101d6b879e6d861c79d504b6da4ad6a3d12064d77d6b3ceda6fcab268079969443f717046491d127757e82ad08f67406e91e7bd6ba95dd59aae75d56a3ddad7c0c24fdd6c6c91fcfd6abf40f218a18e55d88aa54656ec318c8cf2eb561774d7716af8b71e4078210ead9e21c49428e338e7b0deabc36233e7562b8596e662c1b9bfc4b3393f395a76df840e79c72a126442976d1b2ed53998ea75871b79f4b01c4abe452802028743839a499d3624ea055b1853ca434ae175c5b45253d338f2cd3c9699f79d4312cb26e6f4a71a51642943012b03a79f2032692106e0ccdba26e52e5373a3c5e325b7428b83970957955549fe86842558e1b3a784c409464a5442cf127bb20288e5f374e75d688d30ad57ab635af2531b3de4858e8da79fe7ca9bf14d3a63893297f06991dd38c676e223881f6ab0764f7945a3b418a97412dcd418c4819c28ee0fe743236b1ba2d0a72567a1653f3185b36bb2c6619432d2789e7c1eed947209481f32b6f3c5230aeb7089786ad5770cb8a921462cb6070a5c29dd48524f2501bf91a9d1852718e551cab377ba858b9bcf9537199288ec04ec852be6593d4e001e95c44efd3a154852fd694df2ccfdbdc90ec743b8cada3bf3e47cd27a8ea294d377154fb596dd69b6654270c67da6c612952760523f0918229cb8ace1239e6a27481f8872f9387f4e45c1696fd9b0104fe69356c6f42e6becb2d03428eae2cffd9";
//        byte[] b = ttt1.getBytes();
        byte[] b = hex2byte(xxx);
//        byte[] b = StringUtils.hexStringToBytes(xxx);
        if(b.length!=0){
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        }
        else {
            return null;
        }

    }

    public static byte[] hex2byte(String str) { // 字符串转二进制   
        if (str == null)
            return null;
        str = str.trim();
        int len = str.length();
        if (len == 0 || len % 2 == 1)
            return null;
        byte[] b = new byte[len / 2];
        try {
            for (int i = 0; i < str.length(); i += 2) {
                b[i / 2] = (byte) Integer.decode("0X" + str.substring(i, i + 2)).intValue();
            }
            return b;
        } catch (Exception e) {
            return null;
        }
    }




}
