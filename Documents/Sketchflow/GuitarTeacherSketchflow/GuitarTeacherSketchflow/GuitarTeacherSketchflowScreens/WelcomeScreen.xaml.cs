using System;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Documents;
using System.Windows.Ink;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Animation;
using System.Windows.Shapes;

namespace GuitarTeacherSketchflowScreens
{
	public partial class WelcomeScreen : UserControl
	{
		public WelcomeScreen()
		{
			// Required to initialize variables
			InitializeComponent();
			
			//Storyboard ST = this.FindName("anima") as Storyboard;
			//ST.Completed += new EventHandler(AnimaListener);
			Microsoft.Expression.Prototyping.Services.PlayerContext.Instance.ActiveNavigationViewModel.PlaySketchFlowAnimation(  "_SketchFlowAnimation_SketchFlowAnimation",
  "GuitarTeacherSketchflowScreens.Login");
		}
		
		

		//the listener
		void AnimaListener(object sender, EventArgs e)
		{
			Microsoft.Expression.Prototyping.Services.PlayerContext.Instance.ActiveNavigationViewModel.NavigateToScreen("Login", true); 
		}
	}
}