import { useState } from "react";
import { FaEnvelope, FaMapMarkerAlt } from "react-icons/fa";
import { FaPhoneVolume } from "react-icons/fa6";

const Contact = () => {
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [message, setMessage] = useState("");
  const [status, setStatus] = useState(""); // Sukces/błąd

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const response = await fetch("http://localhost:8080/api/public/contact", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ name, email, message }),
      });

      if (response.ok) {
        setStatus("Message sent successfully!");
        setName("");
        setEmail("");
        setMessage("");
      } else {
        const errorText = await response.text();
        setStatus("Error: " + errorText);
      }
    } catch (error) {
      setStatus("Error: " + error.message);
    }
  };

  return (
    <div
      className="flex flex-col items-center justify-center min-h-screen py-12 bg-cover bg-center"
      style={{
        backgroundImage:
          "url('https://res.cloudinary.com/dap1zpgie/image/upload/contact-image_sbwicu.jpg')",
      }}
    >
      <div className="bg-white shadow-lg rounded-lg p-8 w-full max-w-lg">
        <h1 className="text-4xl font-bold text-center mb-6">Contact Us</h1>
        <p className="text-gray-600 text-center mb-4">
          We would love to hear from you! Please fill out the form below or
          contact us directly
        </p>

        <form className="space-y-4" onSubmit={handleSubmit}>
          <div>
            <label className="block text-sm font-medium text-gray-700">
              Name
            </label>
            <input
              type="text"
              value={name}
              onChange={(e) => setName(e.target.value)}
              required
              className="mt-1 block w-full border border-gray-300 rounded-lg p-2 focus:outline-none focus:ring-2 ring-blue-500"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700">
              Email
            </label>
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
              className="mt-1 block w-full border border-gray-300 rounded-lg p-2 focus:outline-none focus:ring-2 ring-blue-500"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700">
              Message
            </label>
            <textarea
              rows="4"
              value={message}
              onChange={(e) => setMessage(e.target.value)}
              required
              className="mt-1 block w-full border border-gray-300 rounded-lg p-2 focus:outline-none focus:ring-2 ring-blue-500"
            />
          </div>

          <button
            type="submit"
            className="w-full bg-blue-500 text-white py-2 rounded-lg hover:bg-blue-600 transition duration-300"
          >
            Send Message
          </button>
        </form>

        {status && (
          <div className="mt-4 text-center text-sm text-blue-700">{status}</div>
        )}

        <div className="mt-8 text-center">
          <h2 className="text-lg font-semibold">Contact Information</h2>
          <div className="flex flex-col items-center space-y-2 mt-4">
            <div className="flex items-center">
              <FaPhoneVolume className="text-red-500 mr-2" />
              <span className="text-gray-600">+48 782 822 793</span>
            </div>

            <div className="flex items-center">
              <FaEnvelope className="text-blue-500 mr-2" />
              <span className="text-gray-600">
                daniel.strielnikow@outlook.com
              </span>
            </div>

            <div className="flex items-center">
              <FaMapMarkerAlt className="text-green-500 mr-2" />
              <span className="text-gray-600">Gliwice, Silesia, Poland</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Contact;
