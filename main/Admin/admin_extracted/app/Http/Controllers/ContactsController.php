<?php

namespace App\Http\Controllers;


use App\Models\Contacts;
use App\Models\UserProfile;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Log;
use App\Imports\ContactsImport;
use Maatwebsite\Excel\Facades\Excel;
use VCardParser;
use Illuminate\Support\Facades\Response;
use Illuminate\Support\Facades\Schema;

class ContactsController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index()
    {
        $contacts = Contacts::orderBy('created_at', 'desc')
            ->whereIn('isSpam', [0])
            ->paginate(10);
        return view('frontend.contacts.index', compact('contacts'));
    }

    public function spamContacts()
    {
        $contacts = Contacts::orderBy('created_at', 'desc')
            ->whereIn('isSpam', [1])
            ->paginate(10);
        return view('frontend.contacts.spam_contacts', compact('contacts'));
    }

    /**
     * Show the form for creating a new resource.
     */
    public function create()
    {
        return view('frontend.contacts.add_contacts');
    }



    public function getImagePath(Request $request)
    {
        $completeFileName = $request->file('image')->getClientOriginalName();
        $fileNameOnly = pathinfo($completeFileName, PATHINFO_FILENAME);
        $extension = $request->file('image')->getClientOriginalExtension();

        $compPic = str_replace(' ', '_', $fileNameOnly) . '_' . time() . '.' . $extension;

        return $request->file('image')->storeAs('public/contacts', $compPic);
    }
    public function store(Request $request)
    {

        $request->validate([
            'name' => 'required',
            'phoneNumber' => 'required',

        ]);

        $contact = new Contacts();

        if ($request->has('isSpam')) {
            $contact->isSpam = 1;
        } else {
            $contact->isSpam = 0;
        }
        if ($request->hasFile('image')) {
            $image_path = $this->getImagePath($request);
            $contact->image = $image_path;
        }



        $contact->name = $request->name;
        $contact->phoneNumber = $request->phoneNumber;
        $contact->spamType = $request->spamType;
        $contact->appuser_id = $request->appuser_id;
        $contact->tag = $request->tag;
        $contact->carrierName = $request->carrierName;
        $contact->countryName = $request->countryName;



        $contact->save();

        return redirect(route('contacts.index'))->with('successMessage', 'Succesfully Added to contact');
    }

    /**
     * Display the specified resource.
     */
    public function show(string $id)
    {
        //
    }

    /**
     * Show the form for editing the specified resource.
     */
    public function edit(string $id)
    {

        $contacts = Contacts::where('id', $id)->first();
        return view('frontend.contacts.edit_contacts', compact('contacts'));
    }

    /**
     * Update the specified resource in storage.
     */
    public function update(Request $request, string $id)
    {

        $contact = Contacts::find($id);

        $request->validate([
            'name' => 'required',
            'phoneNumber' => 'required',

        ]);



        if ($request->has('isSpam')) {
            $contact->isSpam = 1;
        } else {
            $contact->isSpam = 0;
        }
        if ($request->hasFile('image')) {
            $image_path = $this->getImagePath($request);
            $contact->image = $image_path;
        }



        $contact->name = $request->name;
        $contact->phoneNumber = $request->phoneNumber;
        $contact->spamType = $request->spamType;
        $contact->appuser_id = $request->appuser_id;
        $contact->tag = $request->tag;
        $contact->carrierName = $request->carrierName;
        $contact->countryName = $request->countryName;



        $contact->save();

        return redirect(route('contacts.index'))->with('successMessage', 'Contact Edited successfully');
    }

    /**
     * Remove the specified resource from storage.
     */
    public function destroy(string $id)
    {
        Contacts::find($id)->delete();
        return redirect(route('contacts.index'))->with('successMessage', 'Slider Deleted successfully');
    }

    public function getName(Request $request)
    {

        $phoneNumber = $request->input('phoneNumber');


        $profileData = UserProfile::where('phone', $phoneNumber)->first();

        if ($profileData) {
            return response()->json(['model' => $profileData, 'type' => "profile"]);
        }

        $contactsData = Contacts::where('phoneNumber', $phoneNumber)
            ->select('name', 'phoneNumber', 'isSpam', 'spamType', 'appuser_id', 'tag')
            ->groupBy('name', 'phoneNumber', 'isSpam', 'spamType', 'appuser_id', 'tag')
            ->orderByRaw('COUNT(*) DESC')
            ->first();

        if ($contactsData) {
            return response()->json(['model' => $contactsData, 'type' => "contacts"]);
        } else {
            return response()->json(['message' => 'No Contact data found'], 404);
        }
    }

    public function getContacts(Request $request)
    {
        // Log::info('Received contacts request', ['request_data' => $request->all()]);
        $phoneNumber = $request->input('phoneNumber');

        $contacts = Contacts::where('appuser_id', $phoneNumber)->get();

        return response()->json($contacts, 200);
    }

    public function saveContacts(Request $request)
    {
        // Log::info('Received contacts request', ['request_data' => $request->all()]);

        try {
            $contacts = $request->input('contacts');
            $appuser_id = $request->input('appuser_id');

            if (empty($contacts)) {
                return response()->json(['message' => 'No contacts found'], 400);
            }

            foreach ($contacts as $contact) {
                Contacts::create([
                    'appuser_id' => $appuser_id,
                    'name' => $contact['name'],
                    'phoneNumber' => $contact['phoneNumber'],
                ]);
            }

            return response()->json(['message' => 'Contacts saved successfully'], 201);
        } catch (\Exception $e) {
            Log::error('Failed to save contacts: ' . $e->getMessage());
            return response()->json(['message' => 'Internal Server Error'], 500);
        }
    }

    public function saveOneContact(Request $request)
    {
        // Log::info('Received contacts request', ['request_data' => $request->all()]);
        try {
            $contacts = $request->input('contacts');
            $appuser_id = $request->input('appuser_id');

            if (empty($contacts)) {
                return response()->json(['message' => 'No contacts found'], 400);
            }

            foreach ($contacts as $contact) {
                $data = [
                    'name' => $contact['name'],
                    'isSpam' => $contact['isSpam'],

                ];
                if (array_key_exists('spamType', $contact)) {
                    $data['spamType'] = $contact['spamType'];
                }
                if (array_key_exists('carrierName', $contact)) {
                    $data['carrierName'] = $contact['carrierName'];
                }
                if (array_key_exists('countryName', $contact)) {
                    $data['countryName'] = $contact['countryName'];
                }
                if (array_key_exists('tag', $contact)) {
                    $data['tag'] = $contact['tag'];
                }
                Contacts::updateOrCreate(
                    [
                        'appuser_id' => $appuser_id,
                        'phoneNumber' => $contact['phoneNumber']
                    ],
                    $data
                );
            }

            return response()->json(['message' => 'Contacts saved successfully'], 201);
        } catch (\Exception $e) {
            Log::error('Failed to save contacts: ' . $e->getMessage());
            return response()->json(['message' => 'Internal Server Error'], 500);
        }
    }

    public function searchApi(Request $request)
    {
        // Log::info('Received searchTerm request', ['request_data' => $request->all()]);
        $searchTerm = $request->input('searchTerm');


        $userProfiles = DB::table('user_profiles')
            ->select('id', 'first_name as name', 'phone as phoneNumber', DB::raw('"UserProfile" as type'))
            ->where('phone', 'LIKE', "%{$searchTerm}%")
            ->orWhere('first_name', 'LIKE', "%{$searchTerm}%")
            ->orWhere('last_name', 'LIKE', "%{$searchTerm}%")
            ->get();


        $userProfilesArray = $userProfiles->toArray();


        $userProfilePhoneNumbers = collect($userProfilesArray)->pluck('phoneNumber')->toArray();


        $contacts = DB::table('contacts')
            ->select('id', 'name', 'phoneNumber', DB::raw('"Contact" as type'), DB::raw('COUNT(*) as count'))
            ->where(function ($query) use ($searchTerm) {
                $query->where('phoneNumber', 'LIKE', "%{$searchTerm}%")
                    ->orWhere('name', 'LIKE', "%{$searchTerm}%");
            })
            ->groupBy('id', 'name', 'phoneNumber')
            ->orderBy('count', 'DESC')
            ->get();


        $filteredContacts = $contacts->filter(function ($contact) use ($userProfilePhoneNumbers) {
            return !in_array($contact->phoneNumber, $userProfilePhoneNumbers);
        });

        $finalResults = collect($userProfilesArray)->merge($filteredContacts);

        return response()->json($finalResults);
    }

    public function getContactformphoneNumber(Request $request)
    {
        $phoneNumber = $request->input('phoneNumber');

        $profileData = UserProfile::where('phone', $phoneNumber)->first();

        if ($profileData) {
            $firstName = $profileData->first_name;
            $lastName = $profileData->last_name;

            if (!empty($firstName) && !empty($lastName)) {
                return response()->json(['model' => $profileData, 'type' => "profile"]);
            } elseif (!empty($firstName) || !empty($lastName)) {
                $profileData->first_name = $firstName ?? '';
                $profileData->last_name = $lastName ?? '';
                return response()->json(['model' => $profileData, 'type' => "profile"]);
            }
        }

        $contactsData = Contacts::where('phoneNumber', $phoneNumber)
            ->select('name', 'phoneNumber', 'isSpam', 'spamType', 'appuser_id', 'tag')
            ->groupBy('name', 'phoneNumber', 'isSpam', 'spamType', 'appuser_id', 'tag')
            ->orderByRaw('COUNT(*) DESC')
            ->first();

        if ($contactsData) {
            return response()->json(['model' => $contactsData, 'type' => "contacts"]);
        } else {
            return response()->json(['message' => 'No Contact data found'], 404);
        }
    }


    public function getSearchContact(Request $request)
    {
        Log::info('Received getSearchContact', ['request_data' => $request->all()]);
        $id = $request->input('id');
        $type = $request->input('type');

        if ($type === 'UserProfile') {
            $result = UserProfile::find($id);
            if ($result) {
                $response = [
                    'type' => 'UserProfile',
                    'model' => $result
                ];
            }
        } elseif ($type === 'Contact') {
            $result = Contacts::find($id);
            if ($result) {
                $response = [
                    'type' => 'Contact',
                    'model' => $result
                ];
            }
        } else {
            return response()->json(['error' => 'Invalid type specified'], 400);
        }

        if (isset($response)) {
            return response()->json($response);
        } else {
            return response()->json(['error' => 'Record not found'], 404);
        }
    }

    public function search(Request $request)
    {
        $query = $request->input('query');
        //Log::info('Search request:', ['request' => $request]);
        if (is_null($query) || strlen($query) < 3) {
            return redirect(route('contacts.index'))->with('errorMessage', 'You need to search with at least 3 characters.');
        } else {


            $search_contacts = Contacts::where('name', 'LIKE', "%{$query}%")
                ->orWhere('phoneNumber', 'LIKE', "%{$query}%")
                ->orWhere('tag', 'LIKE', "%{$query}%")
                ->orWhere('carrierName', 'LIKE', "%{$query}%")
                ->orWhere('countryName', 'LIKE', "%{$query}%")
                ->paginate(30);


            //Log::info('Search query:', ['query' => $query, 'contacts' => $contacts]);


            return view('frontend.contacts.search', compact('search_contacts'));
        }
    }

    public function import(Request $request)
    {

        $request->validate([
            'file2' => 'required|mimes:xls,xlsx',
        ]);

        Excel::import(new ContactsImport, $request->file('file2'));


        return redirect(route('contacts.index'))->with('successMessage', 'Contacts imported successfully');
    }


    public function bulk(Request $request)
    {
        return view('frontend.contacts.add_bulk_contacts');
    }



    public function exportSql()
    {
        // Define the table name
        $table = 'contacts';

        // Check if the table exists
        if (!Schema::hasTable($table)) {
            return redirect()->back()->with('error', 'Table does not exist.');
        }

        // Get the table data
        $data = DB::table($table)->get();

        // Generate SQL dump
        $sql = "-- Exported SQL Dump for Table: $table\n\n";
        $sql .= "DROP TABLE IF EXISTS `$table`;\n\n";

        // Get the table structure
        $createTable = DB::select("SHOW CREATE TABLE $table")[0]->{'Create Table'};
        $sql .= $createTable . ";\n\n";

        // Insert data
        foreach ($data as $row) {
            $columns = array_keys((array) $row);
            $values = array_map(function ($value) {
                return is_null($value) ? 'NULL' : "'" . addslashes($value) . "'";
            }, (array) $row);

            $sql .= "INSERT INTO `$table` (`" . implode('`, `', $columns) . "`) VALUES (" . implode(', ', $values) . ");\n";
        }

        // Set the file name
        $fileName = $table . '-' . date('Y-m-d') . '.sql';

        // Return the file as a download
        return Response::make($sql, 200, [
            'Content-Type' => 'application/sql',
            'Content-Disposition' => 'attachment; filename="' . $fileName . '"',
        ]);
    }
    public function importSql(Request $request)
    {
        // Validate the uploaded file
        $request->validate([
            'sql_file' => 'required|file|mimes:sql,txt',
        ]);

        // Get the uploaded file
        $file = $request->file('sql_file');

        // Read the file contents
        $sql = file_get_contents($file->getRealPath());

        // Log file path and content

        if (empty(trim($sql))) {
            return redirect(route('contacts.bulk'))->with('errorMessage', 'The uploaded SQL file is empty.');
        }

        try {
            // Disable foreign key checks before running the SQL script
            DB::statement('SET FOREIGN_KEY_CHECKS=0;');

            // Execute the SQL file directly without transaction handling
            DB::unprepared($sql);

            // Re-enable foreign key checks
            DB::statement('SET FOREIGN_KEY_CHECKS=1;');

            return redirect(route('contacts.bulk'))->with('successMessage', 'SQL file imported successfully!');
        } catch (\Exception $e) {
           // Log::error('SQL Import Error: ' . $e->getMessage());

            return redirect(route('contacts.bulk'))->with('errorMessage', 'Failed to import SQL file. Please check the file format and try again.');
        }
    }


}
