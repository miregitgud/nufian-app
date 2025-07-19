/* eslint-disable max-len */
const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

exports.sendCommentNotification = functions.firestore
    .document("posts/{postId}/comments/{commentId}")
    .onCreate(async (snap, context) => {
      const comment = snap.data();
      const postId = context.params.postId;

      const postSnap = await admin.firestore().doc(`posts/${postId}`).get();
      const post = postSnap.data();
      if (!post || !post.ownerId) return;

      const userSnap = await admin.firestore().doc(`users/${post.ownerId}`).get();
      const userData = userSnap.data();
      const fcmToken = userData && userData.fcmToken;
      if (!fcmToken) return;

      const payload = {
        data: {
          forumId: postId,
          forumUserPostId: comment.userId || "",
          notificationType: "comment",
          title: "New Comment on Your Post",
          body: comment.text || "Someone commented on your post",
        },
      };

      try {
        await admin.messaging().sendToDevice(fcmToken, payload);
        console.log("✅ Notification sent to", fcmToken);
      } catch (error) {
        console.error("❌ Error sending notification:", error);
      }
    });

exports.setAdminRole = functions.https.onCall(async (data, context) => {
  const uid = data.uid;
  if (!uid) {
    throw new functions.https.HttpsError("invalid-argument", "Missing UID");
  }

  await admin.auth().setCustomUserClaims(uid, {admin: true});

  return {message: `Admin claim added to user ${uid}`};
});
