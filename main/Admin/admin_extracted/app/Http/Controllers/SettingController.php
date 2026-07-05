<?php

namespace App\Http\Controllers;

use App\Models\Ads;
use App\Models\Setting;
use App\Models\Notification;
use Illuminate\Http\Request;



class SettingController extends Controller
{

    protected $demoMode;
    public function __construct()
    {
        $this->demoMode = env('DEMO_MODE');
    }
    public function notification_index()
    {
        $setting = Setting::find(1);
        $notifications = Notification::orderBy('created_at', 'desc')->paginate(8);

        return view('frontend.notification.notification', compact(['setting', 'notifications']));
    }



    public function notification_Post(Request $request)
    {
        if ($this->demoMode == 'true') {
            return redirect(route('notification.index'))->with('errorMessage', 'Demo mode is enabled. Changes are not allowed.');

        } else {
            $request->validate([
                'app_id' => 'required',
                'rest_key' => 'required',

            ]);

            $setting = Setting::find(1);


            $setting->onesignal_app_id = $request->app_id;
            $setting->onesignal_rest_key = $request->rest_key;
            $setting->save();

            return redirect(route('notification.index'))->with('successMessage', 'notification Edited successfully');
        }

    }


    public function notification_delete($id)
    {
        $notification = Notification::find($id);
        $notification->delete();

        return redirect(route('notification.index'))->with('successMessage', 'Notification Deleted successfully');

    }

    public function appUpdate()
    {
        $setting = Setting::find(1);
        return view('frontend.appUpdate.index', compact('setting'));
    }

    public function updatePost(Request $request)
    {
        $request->validate([
            //'app_update_status' => 'accepted',
            'app_new_version' => 'required',
            'app_update_desc' => 'required',
            'app_redirect_url' => 'required',

        ]);

        $setting = Setting::find(1);

        //$setting = new Setting();
        if ($request->has('app_update_status')) {
            $setting->app_update_status = 1;
        } else {
            $setting->app_update_status = 0;
        }


        $setting->app_new_version = $request->app_new_version;
        $setting->app_redirect_url = $request->app_redirect_url;
        $setting->app_update_desc = $request->app_update_desc;

        $setting->save();
        return redirect(route('app.update'))->with('successMessage', 'Ads Update Edited successfully');

    }

    public function settings()
    {
        $setting = Setting::find(1);

        return view('frontend.settings.index', compact(['setting']));
    }

    public function about(Request $request)
    {
        $setting = Setting::find(1);

        if ($this->demoMode == 'true') {

            return redirect(route('settings'))->with('errorMessage', 'Demo mode is enabled. Changes are not allowed.');

        } else {

            $request->validate([
                'app_email' => 'required',
                'app_description' => 'required',
                'app_author' => 'required',
                'app_contact' => 'required',
                'app_website' => 'required',
                'app_developed_by' => 'required',

            ]);





            $setting->app_email = $request->app_email;
            $setting->app_description = $request->app_description;
            $setting->app_author = $request->app_author;
            $setting->app_contact = $request->app_contact;
            $setting->app_website = $request->app_website;
            $setting->app_developed_by = $request->app_developed_by;

            $setting->save();


            return redirect(route('settings'))->with('successMessage', 'About Edited successfully');

        }
    }

    public function privacyPolicy(Request $request)
    {
        if ($this->demoMode == 'true') {

            return redirect(route('settings'))->with('errorMessage', 'Demo mode is enabled. Changes are not allowed.');

        } else {
            $setting = Setting::find(1);

            $setting->privacy_policy = $request->privacy_policy;
            $setting->save();

            return redirect(route('settings'))->with('successMessage', 'Privacy Policys Edited successfully');
        }

    }

    public function settingsPost(Request $request)
    {
        $setting = Setting::find(1);

        if ($request->has('isMaintenance')) {
            $setting->isMaintenance = 1;
        } else {
            $setting->isMaintenance = 0;
        }

        $setting->more_apps_url = $request->more_apps_url;
        $setting->save();


        return redirect(route('settings'))->with('successMessage', 'App settings Edited successfully');


    }

    public function api()
    {
        $settings = Setting::find(1)->all();
        $ads = Ads::find(1)->all();




        $response = [
            "ads" => $ads,
            "settings" => $settings,
        ];

        return ($response);

    }

    public function getImagePath(Request $request)
    {
        $completeFileName = $request->file('image')->getClientOriginalName();
        $fileNameOnly = pathinfo($completeFileName, PATHINFO_FILENAME);
        $extension = $request->file('image')->getClientOriginalExtension();

        $compPic = str_replace(' ', '_', $fileNameOnly) . '_' . time() . '.' . $extension;

        return $request->file('image')->storeAs('public/notification', $compPic);
    }

    public function notification_Send(Request $request, Notification $notification)
    {

        $request->validate([
            'title' => 'required',
            'image' => 'required',

        ]);

        $settings = Setting::find(1)->all();



        foreach ($settings as $setting) {
            $oneSignalAppId = $setting->onesignal_app_id;
            $oneSignalRestApiKey = $setting->onesignal_rest_key;

        }

        $title = $request["title"];
        $message = $request["message"];
        $link = $request["link"];



        if ($request->hasFile('image')) {
            $image_path = $this->getImagePath($request);
            $notification->image = $image_path;
        }

        $notification->title = $title;
        $notification->message = $message;
        $notification->url = $link;

        $notification->save();



        $bigImage = $image_path;

        $uniqueId = rand(1000, 9999);

        $content = ["en" => $message];

        $fields = [
            "app_id" => $oneSignalAppId,
            "included_segments" => ["All"],
            "data" => [
                "link" => $link,
                "unique_id" => $uniqueId,
            ],
            "headings" => ["en" => $title],
            "contents" => $content,
            "big_picture" => $bigImage,
            "url" => $link,
        ];

        $fields = json_encode($fields);
        print "\nJSON sent:\n";
        print $fields;

        $ch = curl_init();
        curl_setopt($ch, CURLOPT_URL, "https://onesignal.com/api/v1/notifications");
        curl_setopt($ch, CURLOPT_HTTPHEADER, [
            "Content-Type: application/json; charset=utf-8",
            "Authorization: Basic " . $oneSignalRestApiKey,
        ]);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_HEADER, false);
        curl_setopt($ch, CURLOPT_POST, true);
        curl_setopt($ch, CURLOPT_POSTFIELDS, $fields);
        curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);

        $response = curl_exec($ch);
        curl_close($ch);


        return redirect(route('notification.index'))->with('successMessage', 'notification Send successfully');



    }


    public function notification_Resend(Request $request)
    {

        $request->validate([
            'title' => 'required',

        ]);

        $settings = Setting::find(1);

        $oneSignalAppId = $settings->onesignal_app_id;
        $oneSignalRestApiKey = $settings->onesignal_rest_key;

        $title = $request->input('title');
        $message = $request->input('message');
        $link = $request->input('link');



        $bigImage = $request->input('current_image');

        $uniqueId = rand(1000, 9999);

        $content = ["en" => $message];

        $fields = [
            "app_id" => $oneSignalAppId,
            "included_segments" => ["All"],
            "data" => [
                "link" => $link,
                "unique_id" => $uniqueId,
            ],
            "headings" => ["en" => $title],
            "contents" => $content,
            "big_picture" => $bigImage,
            "url" => $link,
        ];

        $fields = json_encode($fields);

        $ch = curl_init();
        curl_setopt($ch, CURLOPT_URL, "https://onesignal.com/api/v1/notifications");
        curl_setopt($ch, CURLOPT_HTTPHEADER, [
            "Content-Type: application/json; charset=utf-8",
            "Authorization: Basic " . $oneSignalRestApiKey,
        ]);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_HEADER, false);
        curl_setopt($ch, CURLOPT_POST, true);
        curl_setopt($ch, CURLOPT_POSTFIELDS, $fields);
        curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);

        $response = curl_exec($ch);
        curl_close($ch);

        if ($response === false) {
            return redirect(route('notification.index'))->with('errorMessage', 'Failed to send notification');
        }

        $responseData = json_decode($response, true);

        if (isset($responseData['errors'])) {
            return redirect(route('notification.index'))->with('errorMessage', 'Failed to send notification: ' . $responseData['errors'][0]);
        }

        return redirect(route('notification.index'))->with('successMessage', 'Notification sent successfully');
    }
    public function ads(Request $request)
    {

        $ads = Ads::find(1);

        $ads->ad_status = $request->ad_status;
        $ads->main_ads = $request->main_ads;
        $ads->admob_publisher_id = $request->admob_publisher_id;
        $ads->admob_banner_unit_id = $request->admob_banner_unit_id;
        $ads->admob_interstitial_unit_id = $request->admob_interstitial_unit_id;
        $ads->admob_native_unit_id = $request->admob_native_unit_id;
        $ads->admob_app_open_unit_id = $request->admob_app_open_unit_id;
        $ads->unity_game_id = $request->unity_game_id;
        $ads->unity_banner_placement_id = $request->unity_banner_placement_id;
        $ads->unity_interstitial_placement_id = $request->unity_interstitial_placement_id;

        $ads->save();


        return redirect(route('settings'))->with('successMessage', 'Ads Setting Edited successfully');



    }



}



