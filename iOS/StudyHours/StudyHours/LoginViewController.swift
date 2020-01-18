//
//  LoginViewController.swift
//  StudyHours
//
//  Created by Shuang Li on 1/16/20.
//  Copyright © 2020 Shuang Li. All rights reserved.
//

import UIKit
import FirebaseAuth
import FirebaseDatabase

class LoginViewController: UIViewController {
    
    @IBOutlet weak var loginButton: UIButton!
    @IBOutlet weak var emailField: UITextField!
    @IBOutlet weak var passwordField: UITextField!
    @IBOutlet weak var adminCode: UITextField!
    @IBOutlet weak var sisterName: UITextField!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.navigationItem.setHidesBackButton(true, animated: false)
        loginButton.layer.cornerRadius = 10
    }
    
    override func viewDidAppear(_ animated: Bool) {
        if Auth.auth().currentUser != nil {
            self.performSegue(withIdentifier: "login", sender: self)
        }
    }
    
    @IBAction func SignIn(_ sender: Any) {
        Auth.auth().signIn(withEmail: emailField.text!, password: passwordField.text!) { (user, error) in
            if error == nil{
                self.performSegue(withIdentifier: "login", sender: self)
            }else{
                print(error!)
                let alertController = UIAlertController(title: "Error", message: error?.localizedDescription, preferredStyle: .alert)
                let defaultAction = UIAlertAction(title: "OK", style: .cancel, handler: nil)
                
                alertController.addAction(defaultAction)
                self.present(alertController, animated: true, completion: nil)
            }
        }
    }
    
    @IBAction func SignUp(_ sender: Any) {
        Auth.auth().createUser(withEmail: emailField.text!, password: passwordField.text!) { authResult, error in
            if error == nil{
                var ref: DatabaseReference!
                ref = Database.database().reference()
                let user = Auth.auth().currentUser
                let inputAdminCode = Int(self.adminCode.text!)
                let userID = Auth.auth().currentUser?.uid
                
                ref.observeSingleEvent(of: .value, with: { (snapshot) in
                    // Get user value
                    let value = snapshot.value as? NSDictionary
                    let adminCode = value?["adminCode"] as? Int
                    print(adminCode)
                    var post: [String : Any]
                    if adminCode == inputAdminCode{
                        post = ["admin": true]
                    }else{
                        post = ["admin": false]
                    }
                    ref.child("users").child(userID!).setValue(post)
                  }) { (error) in
                    print(error.localizedDescription)
                }
                let sisterNameText = self.sisterName.text!
                var post: [String : Any]
                post = ["sisterName": sisterNameText,
                        "numSessions": 0,
                        "totalHours": 0]
                ref.child("hours").child(userID!).setValue(post)
                // Move to main screen
                self.performSegue(withIdentifier: "login", sender: self)
            }else{
                print(error!)
                let alertController = UIAlertController(title: "Error", message: error?.localizedDescription, preferredStyle: .alert)
                let defaultAction = UIAlertAction(title: "OK", style: .cancel, handler: nil)
                
                alertController.addAction(defaultAction)
                self.present(alertController, animated: true, completion: nil)
            }
        }
    }
    
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {   //delegate method
        textField.resignFirstResponder()
        return true
    }
//    }
    
    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
    }
    */
}
