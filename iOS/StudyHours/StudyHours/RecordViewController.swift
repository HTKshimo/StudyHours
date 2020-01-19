//
//  RecordViewController.swift
//  StudyHours
//
//  Created by Shuang Li on 1/16/20.
//  Copyright © 2020 Shuang Li. All rights reserved.
//

import UIKit
import FirebaseAuth
import FirebaseDatabase

class RecordViewController: UIViewController {

    @IBOutlet weak var checkInTime: UILabel!
    @IBOutlet weak var elapsedTime: UILabel!
    @IBOutlet weak var startButton: UIButton!
    @IBOutlet weak var actIndicator: UIActivityIndicatorView!
    
    var ref = Database.database().reference()
    let user = Auth.auth().currentUser
    var recording : Bool = false
    
    override func viewDidLoad() {
        super.viewDidLoad()
        actIndicator.startAnimating()
        self.navigationItem.setHidesBackButton(true, animated: false)
        
        ref.child("hours").child(String(user!.uid)).observe(.value) { snapshot in
            var `in` : Int64 = 0
            var numSessions : Int? = nil
            for child in (snapshot.children.allObjects as! [DataSnapshot]){
                if (child.key == "numSessions"){
                    numSessions = child.value! as? Int
                }
            }
            for child in (snapshot.children.allObjects as! [DataSnapshot]){
//                print(child.key)
                if (Int(child.key) == numSessions! + 1){
//                    print("if (Int(child.key) == self.numSessions! + 1) key: \(child.key)")
                    self.recording = true
                    for elem in (child.children.allObjects as! [DataSnapshot]){
                        if (elem.key == "in"){
                            `in` = elem.value as! Int64
                        }
                    }
                }
            }
//            print("observe self.recording: \(self.recording)")
//            print(`in`)
            self.updateScreen(self.recording, `in`)
            self.actIndicator.stopAnimating()
        }
    }
    
    func updateScreen(_ recording : Bool, _ inTime : Int64) {
        if (recording){
            let dateIn = Date(timeIntervalSince1970: TimeInterval(Double(inTime/1000)))
//            let interval = Date().timeIntervalSince(dateIn)
            let dateFormatter = DateFormatter()
//            let intervalFormatter = DateFormatter()
            dateFormatter.dateFormat = "MM/dd/yyyy HH:mm"
            let dateInFormatted = dateFormatter.string(from: dateIn)
//            let currentDateFormatted = dateFormatter.string(from: currentDate)
//            cell.textLabel?.text = dateInFormatted + " - " + dateOutFormatted
            checkInTime.text = "Checked In: \n " + dateInFormatted
            checkInTime.adjustsFontSizeToFitWidth = true
            elapsedTime.text = "Duration:\n 00:00"
            elapsedTime.adjustsFontSizeToFitWidth = true
        }else{
            checkInTime.text = "Checked In: \n (have not checked in)"
            elapsedTime.text = "Duration:\n 00:00"
        }
        if (self.recording){
            startButton.setTitle("Stop", for: .normal)
        }else{
            startButton.setTitle("Start", for: .normal)
        }
    }
    
    @IBAction func logout(_ sender: Any) {
        let firebaseAuth = Auth.auth()
        do {
            try firebaseAuth.signOut()
        } catch let signOutError as NSError {
          print ("Error signing out: %@", signOutError)
        }
        self.performSegue(withIdentifier: "logout", sender: self)
    }
    
    @IBAction func startRecording(_ sender: Any) {
        self.recording = !self.recording
        ref.child("hours/\(String(user!.uid))").observeSingleEvent(of: .value, with: { (snapshot) in
            var `in` : Int64 = 0
            let value = snapshot.value as? NSDictionary
            let numSessions = value?["numSessions"] as? Int
            let totalHours = value?["totalHours"] as? Double
            if (self.recording){ //Just Started Recording by clicking the button
                `in` = Int64(NSDate().timeIntervalSince1970 * 1000)
                //store `in` in database
                self.ref.child("hours/\(String(self.user!.uid))/\(String(describing: numSessions!+1))/in").setValue(`in`)
            }else{ //Just finished recording by clicking the button
                self.ref.child("hours/\(String(self.user!.uid))/\(String(numSessions!+1))").observeSingleEvent(of: .value, with: { (snapshot) in
                    let value = snapshot.value as? NSDictionary
                    `in` = (value?["in"] as? Int64)!
//                    print("`in`: \(`in`)")
                    let out = Int64(NSDate().timeIntervalSince1970 * 1000)
                    let interval = out - `in`
                    if (interval > 1800000){
                        //TODO: Popup to ask if want to submit this session
                        let alertC = UIAlertController(title: "Confirm", message: "Would you like to submit this session?", preferredStyle: .alert)
                        alertC.addAction(UIAlertAction(title: "Yes", style: .default, handler: { (action: UIAlertAction!) in
                            self.recordSession(interval, numSessions!, out, totalHours!)
                        }))
                        alertC.addAction(UIAlertAction(title: "No", style: .cancel, handler: { (action: UIAlertAction!) in
                            self.dismissSession(numSessions!)
                        }))
                        self.present(alertC, animated: true, completion: nil)
                    }else{
                        //popup reminding that this session is less than 30 mins hence was not recorded
                        let alertMessage = "This session is not recorded because it lasted less than 30 minutes."
                        let alertController = UIAlertController(title: "Warning", message: alertMessage, preferredStyle: .alert)
                        let defaultAction = UIAlertAction(title: "OK", style: .cancel, handler: nil)
                        alertController.addAction(defaultAction)
                        self.present(alertController, animated: true, completion: nil)
                        self.dismissSession(numSessions!)
                    }
                }) { (error) in
                    print(error.localizedDescription)
                }
            }
            self.updateScreen(self.recording, `in`)
        }) { (error) in
            print(error.localizedDescription)
        }
    }
    
    func recordSession(_ interval : Int64, _ numSessionsIn : Int, _ out : Int64, _ totalHoursIn : Double) {
        let numHours = Double(interval) / 3600 / 1000
        let str = String(format: "%.1f", numHours)
//        print("interval: \(interval)")
        let numSessions = numSessionsIn + 1
        //update numSessions
        self.ref.child("hours/\(String(self.user!.uid))/numSessions").setValue(numSessions)
        //add the out time to child with key [numSessions + 1]
        self.ref.child("hours/\(String(self.user!.uid))/\(String(describing: numSessions))/out").setValue(out)
        //update totalHours
        let totalHours = totalHoursIn + Double(str)!
        self.ref.child("hours/\(String(self.user!.uid))/totalHours").setValue(totalHours)
    }
    
    func dismissSession(_ numSessions : Int) {
        //remove the child with key [numSessions + 1]
        self.ref.child("hours/\(String(self.user!.uid))/\(String(describing: numSessions+1))").setValue(nil)
    }
    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
    }
    */

}
