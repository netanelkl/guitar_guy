using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace ChordFingerFinder
{
    class CGuitarHand
    {
        public CFstFinger FirstFinger { get; private set; }
        public CSecFinger SecondFinger { get; private set; }
        public CThrdFinger ThirdFinger { get; private set; }
        public CFourthFinger FourthFinger { get; private set; }

        public List<EString> OpenStrings { get; private set; }

        public List<CFstFinger> lstFirstFinger { get; private set; }
        public List<CSecFinger> lstSecondFinger { get; private set; }
        public List<CThrdFinger> lstThirdFinger { get; private set; }
        public List<CFourthFinger> lstFourthFinger { get; private set; }

        public CGuitarHand()
        {
            FirstFinger = new CFstFinger();
            SecondFinger = new CSecFinger();
            ThirdFinger = new CThrdFinger();
            FourthFinger = new CFourthFinger();

            lstFirstFinger = new List<CFstFinger>();
            lstSecondFinger = new List<CSecFinger>();
            lstThirdFinger = new List<CThrdFinger>();
            lstFourthFinger = new List<CFourthFinger>();

            OpenStrings = new List<EString>();
        }

        //public bool add(cnotefingerposition cpos)
        //{
        //    if (cpos.fingerposition.fret == 0)
        //    {
        //        addtolistifdontexist(openstrings, cpos.fingerposition.string);

        //        return true;
        //    }

        //    if(openstrings.contains(cpos.fingerposition.string))
        //    {
        //        return true;
        //    }

        //    bool fcapo = false;

        //    // try and give the position to the fingers.
        //    if (!firstfinger.add(cpos.fingerposition, out fcapo) || fcapo)
        //    {
        //        if (!secondfinger.add(cpos.fingerposition, out fcapo) || fcapo)
        //        {
        //            if (!thirdfinger.add(cpos.fingerposition, out fcapo) || fcapo)
        //            {
        //                if (!fourthfinger.add(cpos.fingerposition, out fcapo) || fcapo)
        //                {
        //                    return false;
        //                }
        //            }
        //        }
        //    }

        //    postadd(cpos);

        //    return true;
        //}

        public bool Add2(CNoteFingerPosition cPos)
        {
            if (cPos.FingerPosition.Fret == 0)
            {
                AddToListIfDontExist(OpenStrings, cPos.FingerPosition.String);

                return true;
            }

            if (OpenStrings.Contains(cPos.FingerPosition.String))
            {
                return true;
            }

            List<CFstFinger> lstFirstTemp = new List<CFstFinger>(lstFirstFinger);
            foreach (CFstFinger cFngr in lstFirstTemp)
            {
                CFstFinger cNew = (CFstFinger)GetNewFinger(typeof(CFstFinger), cFngr);
                if (cNew.Add(cPos.FingerPosition))
                {
                    AddToListIfDontExist(lstFirstFinger, cNew);
                }
            }
            List<CSecFinger> lstSecTemp = new List<CSecFinger>(lstSecondFinger);
            foreach (CSecFinger cFngr in lstSecTemp)
            {
                CSecFinger cNew = (CSecFinger)GetNewFinger(typeof(CSecFinger), cFngr);
                if (cNew.Add(cPos.FingerPosition))
                {
                    AddToListIfDontExist(lstSecondFinger, cNew);
                }
            }
            List<CThrdFinger> lstThrdTemp = new List<CThrdFinger>(lstThirdFinger);
            foreach (CThrdFinger cFngr in lstThrdTemp)
            {
                CThrdFinger cNew = (CThrdFinger)GetNewFinger(typeof(CThrdFinger), cFngr);
                if (cNew.Add(cPos.FingerPosition))
                {
                    AddToListIfDontExist(lstThirdFinger, cNew);
                }
            }
            List<CFourthFinger> lstFourthTemp = new List<CFourthFinger>(lstFourthFinger);
            foreach (CFourthFinger cFngr in lstFourthTemp)
            {
                CFourthFinger cNew = (CFourthFinger)GetNewFinger(typeof(CFourthFinger), cFngr);
                if (cNew.Add(cPos.FingerPosition))
                {
                    AddToListIfDontExist(lstFourthFinger, cNew);
                }
            }

            CFstFinger cFstFinger = (CFstFinger)GetNewFinger(typeof(CFstFinger));
            if (cFstFinger.Add(cPos.FingerPosition))
            {
                AddToListIfDontExist(lstFirstFinger, cFstFinger);
            }

            CSecFinger cSecFinger = (CSecFinger)GetNewFinger(typeof(CSecFinger));
            if (cSecFinger.Add(cPos.FingerPosition))
            {
                AddToListIfDontExist(lstSecondFinger, cSecFinger);
            }

            CThrdFinger cThrdFinger = (CThrdFinger)GetNewFinger(typeof(CThrdFinger));
            if (cThrdFinger.Add(cPos.FingerPosition))
            {
                AddToListIfDontExist(lstThirdFinger, cThrdFinger);
            }

            CFourthFinger cFourthFinger = (CFourthFinger)GetNewFinger(typeof(CFourthFinger));
            if (cFourthFinger.Add(cPos.FingerPosition))
            {
                AddToListIfDontExist(lstFourthFinger, cFourthFinger);
            }

            return true;

        }


        private void PostAdd(CNoteFingerPosition cPos)
        {
            PostAddFinger(FirstFinger, cPos);
            PostAddFinger(SecondFinger, cPos);
            PostAddFinger(ThirdFinger, cPos);
            PostAddFinger(FourthFinger, cPos);
        }

        private void PostAddFinger(CGuitarFinger cFinger,  CNoteFingerPosition cPos)
        {
            if (IsValidFret(cFinger.Fret) &&
                cPos.FingerPosition.Fret > cFinger.Fret)
            {
                cFinger.Add(new CFingerPosition(cPos.FingerPosition.String, cFinger.Fret));
            }
        }

        public override string ToString()
        {
            string sResult = String.Empty;

            string sFingerString = String.Empty;

            string sFirstString = String.Empty;
            string sSecondString = String.Empty;
            string sThirdString = String.Empty;
            string sFourthString = String.Empty;
            string sFifthString = String.Empty;
            string sSixthString = String.Empty;

            sFingerString += "First finger: " + FirstFinger.ToString();
            BuildStringOnStrings(FirstFinger, ref sFirstString, ref sSecondString, ref sThirdString, ref sFourthString, ref sFifthString, ref sSixthString);
            sFingerString += "Second finger: " + SecondFinger.ToString();
            BuildStringOnStrings(SecondFinger, ref sFirstString, ref sSecondString, ref sThirdString, ref sFourthString, ref sFifthString, ref sSixthString);
            sFingerString += "Third finger: " + ThirdFinger.ToString();
            BuildStringOnStrings(ThirdFinger, ref sFirstString, ref sSecondString, ref sThirdString, ref sFourthString, ref sFifthString, ref sSixthString);
            sFingerString += "Fourth finger: " + FourthFinger.ToString();
            BuildStringOnStrings(FourthFinger, ref sFirstString, ref sSecondString, ref sThirdString, ref sFourthString, ref sFifthString, ref sSixthString);

            BuildStringOnStrings(OpenStrings, 0, ref sFirstString, ref sSecondString, ref sThirdString, ref sFourthString, ref sFifthString, ref sSixthString);

            if( sFirstString == String.Empty)
            {
                sFirstString = "x";
            }
            if (sSecondString == String.Empty)
            {
                sSecondString = "x";
            }
            if (sThirdString == String.Empty)
            {
                sThirdString = "x";
            }
            if (sFourthString == String.Empty)
            {
                sFourthString = "x";
            }
            if (sFifthString == String.Empty)
            {
                sFifthString = "x";
            }
            if (sSixthString == String.Empty)
            {
                sSixthString = "x";
            }

            sResult = sSixthString + " " + sFifthString + " " + sFourthString + " " + sThirdString + " " + sSecondString + " " + sFirstString;
            sResult += " \n" + sFingerString;
            return sResult;
        }

        private void BuildStringOnStrings(CGuitarFinger cFinger, ref string sFirstString, ref string sSecondString, ref string sThirdString, ref string sFourthString, ref string sFifthString, ref string sSixthString)
        {
            BuildStringOnStrings(cFinger.Strings, cFinger.Fret, ref sFirstString, ref sSecondString, ref sThirdString, ref sFourthString, ref sFifthString, ref sSixthString);
        }

        private static bool IsValidFret(int nFret)
        {
            return nFret >= 0 && nFret <= Globals.NUMBER_OF_FRETS;
        }

        private static void BuildStringOnStrings(List<EString> lstStrings, int nFret, ref string sFirstString, ref string sSecondString, ref string sThirdString, ref string sFourthString, ref string sFifthString, ref string sSixthString)
        {
            string sToAdd = "x";
            
            if(IsValidFret(nFret))
            {
                sToAdd = nFret.ToString();
            }
            foreach (EString currString in lstStrings)
            {
                switch (currString)
                {
                    case EString.Sixth:
                    {
                        sSixthString += nFret.ToString();
                        break;
                    }
                    case EString.Fifth:
                    {
                        sFifthString += nFret.ToString();
                        break;
                    }
                    case EString.Fourth:
                    {
                        sFourthString += nFret.ToString();
                        break;
                    }
                    case EString.Third:
                     {
                        sThirdString += nFret.ToString();
                        break;
                    }
                    case EString.Second:
                    {
                        sSecondString += nFret.ToString();
                        break;
                    }
                    case EString.First:
                    {
                        sFirstString += nFret.ToString();
                        break;
                    }
                    default:
                    {
                        throw new Exception();
                    }
                }
            }
        }


        public bool GenerateFingerPositioningFromLists()
        {
            // TODO: Need to create a function for sort on each finger.
            // because the first finger priority is alot determined on the higest fret position.
            lstFirstFinger.Sort(CFstFinger.CompareFingers);
            lstSecondFinger.Sort(CSecFinger.CompareFinger);
            lstThirdFinger.Sort(CompareFingers2);
            lstFourthFinger.Sort(CompareFingers2);

            FirstFinger = new CFstFinger();
            if (lstFirstFinger.Count > 0)
            {
                FirstFinger = lstFirstFinger[0];
            }

            CSecFinger cSecFinger = new CSecFinger();

            bool fContinue = false;
            while (true)
            {
                fContinue = false;
                if (lstSecondFinger.Count > 0)
                {
                    cSecFinger = lstSecondFinger[0];

                    if (cSecFinger.Fret < FirstFinger.Fret)
                    {
                        // Remove the move!
                        lstSecondFinger.Remove(cSecFinger);
                        fContinue = true;
                    }
                    if (cSecFinger.Fret >= FirstFinger.Fret)
                    {
                        List<EString> lstBigger = (cSecFinger.Strings.Count >= FirstFinger.Strings.Count) ? (cSecFinger.Strings) : (FirstFinger.Strings);
                        List<EString> lstSmaller = (cSecFinger.Strings.Count < FirstFinger.Strings.Count) ? (cSecFinger.Strings) : (FirstFinger.Strings);

                        foreach (EString eString in lstSmaller)
                        {
                            if (lstBigger.Contains(eString))
                            {
                                // Remove the move!
                                lstSecondFinger.Remove(cSecFinger);
                                fContinue = true;
                          }
                        }
                    }

                    if (fContinue)
                    {
                        continue;
                    }
                }
                else
                {
                    cSecFinger = new CSecFinger();
                }
                break;
            }

            SecondFinger = cSecFinger;

            CThrdFinger cThrdFinger = new CThrdFinger();

            while (true)
            {
                fContinue = false;
                if (lstThirdFinger.Count > 0)
                {
                    cThrdFinger = lstThirdFinger[0];

                   /* if (cThrdFinger.Fret < SecondFinger.Fret)
                    {
                        // Remove the move!
                        lstThirdFinger.Remove(cThrdFinger);
                        fContinue = true;
                    }*/

                    if (cThrdFinger.Fret >= SecondFinger.Fret)
                    {
                        List<EString> lstBigger = (cThrdFinger.Strings.Count >= SecondFinger.Strings.Count) ? (cThrdFinger.Strings) : (SecondFinger.Strings);
                        List<EString> lstSmaller = (cThrdFinger.Strings.Count < SecondFinger.Strings.Count) ? (cThrdFinger.Strings) : (SecondFinger.Strings);

                        foreach (EString eString in lstSmaller)
                        {
                            if (lstBigger.Contains(eString))
                            {
                                // Remove the move!
                                lstThirdFinger.Remove(cThrdFinger);
                                fContinue = true;
                            }
                        }
                    }

                    if (cThrdFinger.Fret < FirstFinger.Fret)
                    {
                        // Remove the move!
                        lstThirdFinger.Remove(cThrdFinger);
                        fContinue = true;
                    }
                    if (cThrdFinger.Fret >= FirstFinger.Fret)
                    {
                        List<EString> lstBigger = (cThrdFinger.Strings.Count >= FirstFinger.Strings.Count) ? (cThrdFinger.Strings) : (FirstFinger.Strings);
                        List<EString> lstSmaller = (cThrdFinger.Strings.Count < FirstFinger.Strings.Count) ? (cThrdFinger.Strings) : (FirstFinger.Strings);

                        foreach (EString eString in lstSmaller)
                        {
                            if (lstBigger.Contains(eString))
                            {
                                // Remove the move!
                                lstThirdFinger.Remove(cThrdFinger);
                                fContinue = true;
                            }
                        }
                    }
               
                    if (fContinue)
                    {
                        continue;
                    }
                }
                else
                {
                    cThrdFinger = new CThrdFinger();
                }

                break;
            }

            ThirdFinger = cThrdFinger;

            CFourthFinger cFourthFinger = new CFourthFinger();

            while (true)
            {
                fContinue = false;
                if (lstFourthFinger.Count > 0)
                {
                    cFourthFinger = lstFourthFinger[0];

                    if (cFourthFinger.Fret < ThirdFinger.Fret)
                    {
                        // Remove the move!
                        lstFourthFinger.Remove(cFourthFinger);
                        fContinue = true;
                    }

                    if (cFourthFinger.Fret >= ThirdFinger.Fret)
                    {
                        List<EString> lstBigger = (cFourthFinger.Strings.Count >= ThirdFinger.Strings.Count) ? (cFourthFinger.Strings) : (ThirdFinger.Strings);
                        List<EString> lstSmaller = (cFourthFinger.Strings.Count < ThirdFinger.Strings.Count) ? (cFourthFinger.Strings) : (ThirdFinger.Strings);

                        foreach (EString eString in lstSmaller)
                        {
                            if (lstBigger.Contains(eString))
                            {
                                // Remove the move!
                                lstFourthFinger.Remove(cFourthFinger);
                                fContinue = true;
                            }
                        }
                    }
                    if (cFourthFinger.Fret < SecondFinger.Fret)
                    {
                        // Remove the move!
                        lstFourthFinger.Remove(cFourthFinger);
                        fContinue = true;
                    }
                    if (cFourthFinger.Fret >= SecondFinger.Fret)
                    {
                        List<EString> lstBigger = (cFourthFinger.Strings.Count >= SecondFinger.Strings.Count) ? (cFourthFinger.Strings) : (SecondFinger.Strings);
                        List<EString> lstSmaller = (cFourthFinger.Strings.Count < SecondFinger.Strings.Count) ? (cFourthFinger.Strings) : (SecondFinger.Strings);

                        foreach (EString eString in lstSmaller)
                        {
                            if (lstBigger.Contains(eString))
                            {
                                // Remove the move!
                                lstFourthFinger.Remove(cFourthFinger);
                                fContinue = true;
                            }
                        }

                    }

                    if (cFourthFinger.Fret < FirstFinger.Fret)
                    {
                        // Remove the move!
                        lstFourthFinger.Remove(cFourthFinger);
                        fContinue = true;
                    }
                    if (cFourthFinger.Fret >= FirstFinger.Fret)
                    {
                        List<EString> lstBigger = (cFourthFinger.Strings.Count >= FirstFinger.Strings.Count) ? (cFourthFinger.Strings) : (FirstFinger.Strings);
                        List<EString> lstSmaller = (cFourthFinger.Strings.Count < FirstFinger.Strings.Count) ? (cFourthFinger.Strings) : (FirstFinger.Strings);

                        foreach (EString eString in lstSmaller)
                        {
                            if (lstBigger.Contains(eString))
                            {
                                // Remove the move!
                                lstFourthFinger.Remove(cFourthFinger);
                                fContinue = true;
                            }
                        }
                    }


                    if (fContinue)
                    {
                        continue;
                    }
                }
                else
                {
                    cFourthFinger = new CFourthFinger();
                }

                break;
            }

            FourthFinger = cFourthFinger;

            return true;
        }

        public bool GenerateFingerPositioning()
        {
            List<CGuitarFinger> lstFourthPlayable = FingerCanPlay(FourthFinger, FourthFinger, ThirdFinger, SecondFinger, FirstFinger);
            List<CGuitarFinger> lstThirdPlayable  = FingerCanPlay(ThirdFinger, ThirdFinger, FourthFinger, SecondFinger, FirstFinger);
            List<CGuitarFinger> lstSecondPlayable = FingerCanPlay(SecondFinger, SecondFinger, ThirdFinger, FourthFinger, FirstFinger);
            List<CGuitarFinger> lstFirstPlayable  = FingerCanPlay(FirstFinger, FirstFinger, SecondFinger, ThirdFinger, FourthFinger);

            // Save the last.
            CFstFinger pTempFirst = new CFstFinger(FirstFinger);
            CSecFinger pTempSecond = new CSecFinger(SecondFinger);
            CThrdFinger pTempThird = new CThrdFinger(ThirdFinger);
            CFourthFinger pTempFourth = new CFourthFinger(FourthFinger);

            List<CGuitarFinger> lstFourthToRemove = new List<CGuitarFinger>();
            List<CGuitarFinger> lstThirdToRemove = new List<CGuitarFinger>();
            List<CGuitarFinger> lstSecondToRemove = new List<CGuitarFinger>();
            List<CGuitarFinger> lstFirstToRemove = new List<CGuitarFinger>();

            foreach (CGuitarFinger cCurrFirstPos in lstFirstPlayable)
            {
                foreach (CGuitarFinger cCurrSecondPos in lstSecondPlayable)
                {
                    foreach (CGuitarFinger cCurrThirdPos in lstThirdPlayable)
                    {
                        foreach (CGuitarFinger cCurrFourthPos in lstFourthPlayable)
                        {
                            CommitPosToRemoveLists<CFstFinger, CSecFinger, CThrdFinger,CFourthFinger>
                                (lstFourthPlayable, 
                                lstThirdPlayable, 
                                lstSecondPlayable, 
                                lstFirstPlayable, 
                                lstFourthToRemove, 
                                lstThirdToRemove, 
                                lstSecondToRemove, 
                                lstFirstToRemove, 
                                cCurrFirstPos, 
                                cCurrSecondPos, 
                                cCurrThirdPos, 
                                cCurrFourthPos);

                            CommitPosToRemoveLists<CFourthFinger, CFstFinger, CSecFinger, CThrdFinger>
                                (lstThirdPlayable,
                                lstSecondPlayable,
                                lstFirstPlayable,
                                lstFourthPlayable,
                                lstThirdToRemove,
                                lstSecondToRemove,
                                lstFirstToRemove,
                                lstFourthToRemove,
                                cCurrFourthPos,
                                cCurrFirstPos,
                                cCurrSecondPos,
                                cCurrThirdPos);

                            CommitPosToRemoveLists<CThrdFinger, CFourthFinger, CFstFinger, CSecFinger>
                                (lstSecondPlayable,
                                lstFirstPlayable,
                                lstFourthPlayable,
                                lstThirdPlayable,
                                lstSecondToRemove,
                                lstFirstToRemove,
                                lstFourthToRemove,
                                lstThirdToRemove,
                                cCurrThirdPos,
                                cCurrFourthPos,
                                cCurrFirstPos,
                                cCurrSecondPos);

                            CommitPosToRemoveLists<CSecFinger, CThrdFinger, CFourthFinger, CFstFinger>
                                (lstFirstPlayable,
                                lstFourthPlayable,
                                lstThirdPlayable,
                                lstSecondPlayable,
                                lstFirstToRemove,
                                lstFourthToRemove,
                                lstThirdToRemove,
                                lstSecondToRemove,
                                cCurrSecondPos,
                                cCurrThirdPos,
                                cCurrFourthPos,
                                cCurrFirstPos);
                        }

                       /* CommitPosToRemove<CThrdFinger, CSecFinger>
                            (lstThirdPlayable, 
                            lstSecondPlayable, 
                            lstThirdToRemove, 
                            lstSecondToRemove, 
                            cCurrSecondPos, 
                            cCurrThirdPos);
                        CommitPosToRemove<CThrdFinger, CFstFinger>
                            (lstThirdPlayable, 
                            lstFirstPlayable, 
                            lstThirdToRemove, 
                            lstFirstToRemove, 
                            cCurrFirstPos, 
                            cCurrThirdPos);*/
                    }

                    /*CommitPosToRemove<CSecFinger, CFstFinger>
                        (lstSecondPlayable, 
                        lstFirstPlayable, 
                        lstSecondToRemove, 
                        lstFirstToRemove, 
                        cCurrFirstPos, 
                        cCurrSecondPos);*/
                }
            }

            int nNumberOfFingers = 0;
            if (lstThirdPlayable.Count > 0)
            {
                nNumberOfFingers++;
            }
            if (lstSecondPlayable.Count > 0)
            {
                nNumberOfFingers++;
            }
            if (lstFirstPlayable.Count > 0)
            {
                nNumberOfFingers++;
            }
            if (lstFourthPlayable.Count > 0)
            {
                nNumberOfFingers++;
            }

            if (!RemoveAllPositions(lstFourthPlayable, lstThirdPlayable, lstSecondPlayable, lstFirstPlayable, lstFourthToRemove, lstThirdToRemove, lstSecondToRemove, lstFirstToRemove, nNumberOfFingers))
            {
                if (!RemoveAllPositions(lstThirdPlayable, lstSecondPlayable, lstFirstPlayable, lstFourthPlayable, lstThirdToRemove, lstSecondToRemove, lstFirstToRemove, lstFourthToRemove, nNumberOfFingers))
                {
                    if (!RemoveAllPositions(lstSecondPlayable, lstFirstPlayable, lstFourthPlayable, lstThirdPlayable, lstSecondToRemove, lstFirstToRemove, lstFourthToRemove, lstThirdToRemove, nNumberOfFingers))
                    {
                        if (!RemoveAllPositions(lstFirstPlayable, lstFourthPlayable, lstThirdPlayable, lstSecondPlayable, lstFirstToRemove, lstFourthToRemove, lstThirdToRemove, lstSecondToRemove, nNumberOfFingers))
                        {
                            throw new Exception("What gives??");
                        }
                    }
                }
            }

            return true;
        }

        private void CommitPosToRemoveLists<TFirst,TSecond,TThird,TFourth>
            (List<CGuitarFinger> lstFourthPlayable, 
             List<CGuitarFinger> lstThirdPlayable,
             List<CGuitarFinger> lstSecondPlayable, 
             List<CGuitarFinger> lstFirstPlayable, 
             List<CGuitarFinger> lstFourthToRemove, 
             List<CGuitarFinger> lstThirdToRemove, 
             List<CGuitarFinger> lstSecondToRemove, 
             List<CGuitarFinger> lstFirstToRemove, 
             CGuitarFinger cCurrFirstPos, 
             CGuitarFinger cCurrSecondPos, 
             CGuitarFinger cCurrThirdPos,
             CGuitarFinger cCurrFourthPos)
    {
        if (lstFourthToRemove.Count == lstFirstPlayable.Count)
        {
            return;
        }

        CommitPosToRemove<TFourth, TThird>
            (lstFourthPlayable,
            lstThirdPlayable,
            lstFourthToRemove,
            lstThirdToRemove,
            cCurrThirdPos,
            cCurrFourthPos);

        CommitPosToRemove<TFourth, TSecond>
            (lstFourthPlayable,
            lstSecondPlayable,
            lstFourthToRemove,
            lstSecondToRemove,
            cCurrSecondPos,
            cCurrFourthPos);

        CommitPosToRemove<TFourth, TFirst>
            (lstFourthPlayable,
                lstFirstPlayable,
                lstFourthToRemove,
                lstFirstToRemove,
                cCurrFirstPos,
                cCurrFourthPos);
        }

        private void CommitPosToRemove<TFstFinger, TSecFinger>
                                      (List<CGuitarFinger> lstFirstPlayable, 
                                       List<CGuitarFinger> lstSecondPlayable, 
                                       List<CGuitarFinger> lstFirstToRemove, 
                                       List<CGuitarFinger> lstSecondToRemove, 
                                       CGuitarFinger cCurrSecondPos, 
                                       CGuitarFinger cCurrFirstPos)
        {
            if (GetPosToRemove<TFstFinger, TSecFinger>(lstFirstToRemove, lstSecondToRemove, cCurrFirstPos, cCurrSecondPos) &&
                cCurrFirstPos.GetIndex() > cCurrSecondPos.GetIndex())
            {
                AddToListIfDontExist(lstFirstToRemove, GetNewFinger(typeof(TFstFinger), cCurrFirstPos));

                /*// Check who has more options.
                if ((lstFirstPlayable.Count - lstFirstToRemove.Count) >= (lstSecondPlayable.Count - lstSecondToRemove.Count))
                {
                    AddToListIfDontExist(lstFirstToRemove, GetNewFinger(typeof(TFstFinger), cCurrFirstPos));
                }
                else
                {
                    AddToListIfDontExist(lstSecondToRemove, GetNewFinger(typeof(TSecFinger), cCurrSecondPos));
                }*/
            }
        }

        public int CompareFingers(CGuitarFinger fst, CGuitarFinger sec)
        {
            return (fst.Priority - sec.Priority);
        }

        public int CompareFingers2(CGuitarFinger fst, CGuitarFinger sec)
        {
            return (sec.Priority - fst.Priority);
        }

        private bool RemoveAllPositions(List<CGuitarFinger> lstFourthPlayable, 
                                        List<CGuitarFinger> lstThirdPlayable, 
                                        List<CGuitarFinger> lstSecondPlayable, 
                                        List<CGuitarFinger> lstFirstPlayable, 
                                        List<CGuitarFinger> lstFourthToRemove, 
                                        List<CGuitarFinger> lstThirdToRemove, 
                                        List<CGuitarFinger> lstSecondToRemove, 
                                        List<CGuitarFinger> lstFirstToRemove, 
                                        int nNumberOfFingers)
        {
            int nNumberOfFingerPositions;
            List<CGuitarFinger> lstFourthTemp;
            List<CGuitarFinger> lstThirdTemp;
            List<CGuitarFinger> lstSecondTemp;
            List<CGuitarFinger> lstFirstTemp;
            InitializingRemoveAll(lstFourthPlayable, 
                                  lstThirdPlayable, 
                                  lstSecondPlayable,
                                  lstFirstPlayable,
                                  lstFourthToRemove,
                                  lstThirdToRemove,
                                  lstSecondToRemove, 
                                  lstFirstToRemove, 
                                  out nNumberOfFingerPositions,
                                  out lstFourthTemp,
                                  out lstThirdTemp, 
                                  out lstSecondTemp, 
                                  out lstFirstTemp);

            RemovePositions(lstFourthTemp, lstThirdTemp, lstSecondTemp, lstFirstTemp, lstFourthToRemove, nNumberOfFingerPositions, nNumberOfFingers);
            if (lstFourthTemp.Count == 0)
            {
                nNumberOfFingers--;
            }
            RemovePositions(lstThirdTemp, lstSecondTemp, lstFirstTemp, lstFourthTemp, lstThirdToRemove, nNumberOfFingerPositions, nNumberOfFingers);
            if (lstThirdTemp.Count == 0)
            {
                nNumberOfFingers--;
            }
            RemovePositions(lstSecondTemp, lstFirstTemp, lstFourthTemp, lstThirdTemp, lstSecondToRemove, nNumberOfFingerPositions, nNumberOfFingers);
            if (lstSecondTemp.Count == 0)
            {
                nNumberOfFingers--;
            }
            RemovePositions(lstFirstTemp, lstFourthTemp, lstThirdTemp, lstSecondTemp, lstFirstToRemove, nNumberOfFingerPositions, nNumberOfFingers);
            

            // Check if there is a finger with to much options.
            lstFourthTemp = new List<CGuitarFinger>(ValidateOneOption(lstFourthTemp, typeof(CFourthFinger)));
            lstThirdTemp = new List<CGuitarFinger>(ValidateOneOption(lstThirdTemp, lstFourthTemp, typeof(CThrdFinger)));

            if (lstThirdTemp.Count > 0)
            {
                lstSecondTemp =  new List<CGuitarFinger>(ValidateOneOption(lstSecondTemp, lstThirdTemp, typeof(CSecFinger)));
            }
            else
            {
                lstSecondTemp = new List<CGuitarFinger>(ValidateOneOption(lstSecondTemp, lstFourthTemp, typeof(CSecFinger)));
            }

            if (lstSecondTemp.Count > 0)
            {
                lstFirstTemp = new List<CGuitarFinger>(ValidateOneOption(lstFirstTemp, lstSecondTemp, typeof(CFstFinger)));
            }
            else
            {
                if (lstThirdTemp.Count > 0)
                {
                    lstFirstTemp = new List<CGuitarFinger>(ValidateOneOption(lstFirstTemp, lstThirdTemp, typeof(CFstFinger)));
                }
                else
                {
                    lstFirstTemp = new List<CGuitarFinger>(ValidateOneOption(lstFirstTemp, lstFourthTemp, typeof(CFstFinger)));
                }
            }

            if (((lstFourthTemp.Count == 0) ||
                 (lstFourthTemp.Count == 1)) &&
                ((lstThirdTemp.Count == 0) ||
                 (lstThirdTemp.Count == 1)) &&
                (lstSecondTemp.Count == 0) ||
                (lstSecondTemp.Count == 1) &&
                (lstFirstTemp.Count == 0) ||
                (lstFirstTemp.Count == 1))
            {

                FirstFinger = new CFstFinger(lstFirstTemp);
                SecondFinger = new CSecFinger(lstSecondTemp);
                ThirdFinger = new CThrdFinger(lstThirdTemp);
                FourthFinger = new CFourthFinger(lstFourthTemp);

                return true;
            }
            
            return false;
        }

        private void InitializingRemoveAll(List<CGuitarFinger> lstFourthPlayable, 
                                           List<CGuitarFinger> lstThirdPlayable, 
                                            List<CGuitarFinger> lstSecondPlayable, 
                                            List<CGuitarFinger> lstFirstPlayable, 
                                            List<CGuitarFinger> lstFourthToRemove, 
                                            List<CGuitarFinger> lstThirdToRemove, 
                                            List<CGuitarFinger> lstSecondToRemove, 
                                            List<CGuitarFinger> lstFirstToRemove, 
                                            out int nNumberOfFingerPositions, 
                                            out List<CGuitarFinger> lstFourthTemp, 
                                            out List<CGuitarFinger> lstThirdTemp, 
                                            out List<CGuitarFinger> lstSecondTemp, 
                                            out List<CGuitarFinger> lstFirstTemp)
        {
            // TODO: Move outside of this function.
            nNumberOfFingerPositions =
                GetNumberOfFingerPositions(lstFourthPlayable,
                                           lstThirdPlayable,
                                           lstSecondPlayable,
                                           lstFirstPlayable);

            lstFirstPlayable.Sort(new Comparison<CGuitarFinger>(CompareFingers));
            lstSecondPlayable.Sort(new Comparison<CGuitarFinger>(CompareFingers));
            lstThirdPlayable.Sort(new Comparison<CGuitarFinger>(CompareFingers));
            lstFourthPlayable.Sort(new Comparison<CGuitarFinger>(CompareFingers));

            lstFourthTemp = new List<CGuitarFinger>(lstFourthPlayable);
            lstThirdTemp = new List<CGuitarFinger>(lstThirdPlayable);
            lstSecondTemp = new List<CGuitarFinger>(lstSecondPlayable);
            lstFirstTemp = new List<CGuitarFinger>(lstFirstPlayable);

            lstFourthToRemove.Sort(new Comparison<CGuitarFinger>(CompareFingers));
            lstThirdToRemove.Sort(new Comparison<CGuitarFinger>(CompareFingers));
            lstSecondToRemove.Sort(new Comparison<CGuitarFinger>(CompareFingers));
            lstFirstToRemove.Sort(new Comparison<CGuitarFinger>(CompareFingers));
        }

        // TODO: This should be implemented in the CFinger classes, each for every finger.
        private static List<CGuitarFinger> ValidateOneOption(List<CGuitarFinger> lstToValidate, Type finger)
        {
            return ValidateOneOption(lstToValidate, null, finger);
        }
        // TODO: This should be implemented in the CFinger classes, each for every finger.
        private static List<CGuitarFinger> ValidateOneOption(List<CGuitarFinger> lstToValidate, List<CGuitarFinger> lstPrevious, Type finger)
        {
            List<CGuitarFinger> result = null;
            if (lstToValidate.Count <= 1)
            {
                result = lstToValidate;
            }
            else
            {
                result = new List<CGuitarFinger>();

                CGuitarFinger cSmallesFret = lstToValidate[0];

                if (lstPrevious       == null ||
                    lstPrevious.Count == 0)
                {
                    foreach (CGuitarFinger cCurrent in lstToValidate)
                    {
                        if (cSmallesFret.Fret > cCurrent.Fret)
                        {
                            cSmallesFret = new CFourthFinger(cCurrent);
                        }
                    }
                }
                else
                {
                    int nSmallestDifference = int.MaxValue;

                    foreach (CGuitarFinger cCurrent in lstToValidate)
                    {
                        int nCurrentDifference = Math.Abs(cCurrent.Fret - lstPrevious[0].Fret);
                        if (nCurrentDifference < nSmallestDifference)
                        {
                            nSmallestDifference = nCurrentDifference;
                            cSmallesFret = GetNewFinger(finger, cCurrent);
                        }
                    }
                }

                result.Add(cSmallesFret);
            }

            return result;
        }

        private static CGuitarFinger GetNewFinger(Type finger, CGuitarFinger cOther)
        {
            if (finger == typeof(CFstFinger))
            {
                return new CFstFinger(cOther);
            }
            if (finger == typeof(CSecFinger))
            {
                return new CSecFinger(cOther);
            }
            if (finger == typeof(CThrdFinger))
            {
                return new CThrdFinger(cOther);
            }
            if (finger == typeof(CFourthFinger))
            {
                return new CFourthFinger(cOther);
            }

            throw new Exception("What the fuck?");
        }

        private static CGuitarFinger GetNewFinger(Type finger)
        {
            if (finger == typeof(CFstFinger))
            {
                return new CFstFinger();
            }
            if (finger == typeof(CSecFinger))
            {
                return new CSecFinger();
            }
            if (finger == typeof(CThrdFinger))
            {
                return new CThrdFinger();
            }
            if (finger == typeof(CFourthFinger))
            {
                return new CFourthFinger();
            }

            throw new Exception("What the fuck?");
        }

        private static int GetNumberOfFingerPositions(List<CGuitarFinger> lstFourthPlayable, List<CGuitarFinger> lstThirdPlayable, List<CGuitarFinger> lstSecondPlayable, List<CGuitarFinger> lstFirstPlayable)
        {
            // Count the number of finger positions to be played.
            List<CGuitarFinger> lstAllPositions = new List<CGuitarFinger>(lstFourthPlayable);

            foreach (CGuitarFinger cCurrThrd in lstThirdPlayable)
            {
                foreach (CGuitarFinger cCurrSec in lstSecondPlayable)
                {
                    foreach (CGuitarFinger cCurrFst in lstFirstPlayable)
                    {
                        AddToListIfDontExist(lstAllPositions, cCurrFst);
                    }
                    AddToListIfDontExist(lstAllPositions, cCurrSec);
                }
                AddToListIfDontExist(lstAllPositions, cCurrThrd);
            }

            return lstAllPositions.Count;
        }

        private static bool CanRemove(CGuitarFinger       lstToCheck, 
                                      List<CGuitarFinger> lstToRemoveFrom, 
                                      List<CGuitarFinger> lstThirdPlayable, 
                                      List<CGuitarFinger> lstSecondPlayable, 
                                      List<CGuitarFinger> lstFirstPlayable)
        {
            // Count the number of singel position fingers.
            List<CGuitarFinger> lstAllPositions = new List<CGuitarFinger>();
            List<CGuitarFinger> lstTemp         = new List<CGuitarFinger>(lstToRemoveFrom);
            if (!lstTemp.Remove(lstToCheck))
            {
                throw new Exception();
            }

            if (lstTemp.Count == 0)
            {
                return true;
            }

            if (lstThirdPlayable.Count == 1)
            {
                AddToListIfDontExist(lstAllPositions, lstThirdPlayable[0]);
            }
            if (lstSecondPlayable.Count == 1)
            {
                AddToListIfDontExist(lstAllPositions, lstSecondPlayable[0]);
            }
            if (lstFirstPlayable.Count == 1)
            {
                AddToListIfDontExist(lstAllPositions, lstFirstPlayable[0]);
            }

            List<EString> lstStringsCollected = new List<EString>();
            foreach (CGuitarFinger cToCheck in lstAllPositions)
            {
                AddToListIfDontExist(lstStringsCollected, cToCheck.Strings);
            }

            foreach (CGuitarFinger cCurrFinger in lstTemp)
            {
                // Now let's check the strings.
                foreach (EString eCurrString in cCurrFinger.Strings)
                {
                    if (!lstStringsCollected.Contains(eCurrString))
                    {
                        return true;
                    }
                }
            }

            // Now check the one we took off.
            // Now let's check the strings.
            foreach (EString eStringToCheck in lstToCheck.Strings)
            {
                if (lstStringsCollected.Contains(eStringToCheck))
                {
                    return true;
                }
            }

            return false;
        }

        private static void RemovePositions(List<CGuitarFinger> lstToPlay, 
                                            List<CGuitarFinger> lstThirdPlayable, 
                                            List<CGuitarFinger> lstSecondPlayable, 
                                            List<CGuitarFinger> lstFirstPlayable, 
                                            List<CGuitarFinger> lstToRemove,
                                            int nNumberOfFingerPositions, 
                                            int nNumberOfFingers)
        {
            int nSpare = nNumberOfFingers - nNumberOfFingerPositions;

            // Check all the moves to remove.
            foreach (CGuitarFinger cToRemove in lstToRemove)
            {
                if (!CanRemove(cToRemove, lstToPlay, lstThirdPlayable, lstSecondPlayable, lstFirstPlayable))
                {
                    continue;
                }

                if (lstToPlay.Count == 1)
                {
                    if (nSpare == 0)
                    {
                        return;
                    }
                }

                // Check that another finger can play it before we remove.
                if ((CanRemoveByPriority(lstToPlay, lstFirstPlayable, cToRemove)) ||
                    (CanRemoveByPriority(lstToPlay, lstSecondPlayable, cToRemove)) ||
                    (CanRemoveByPriority(lstToPlay, lstThirdPlayable, cToRemove)))
                {
                    if (!lstToPlay.Remove(cToRemove))
                    {
                        throw new Exception();
                    }
                }
            }

            lstToRemove.Clear();
        }

        private static bool CanRemoveByPriority(List<CGuitarFinger> lstToPlay, List<CGuitarFinger> lstFirstPlayable, CGuitarFinger cToRemove)
        {
            CGuitarFinger first =
                lstFirstPlayable.Find
                (
                    delegate(CGuitarFinger cFinger)
                    {
                        return cFinger.Equals(cToRemove);
                    }
                );

            if (first == null)
            {
                return false;
            }

            if (lstToPlay.Count == 1)
            {
                if (cToRemove.Priority > first.Priority ||
                    ((cToRemove.Priority == first.Priority) &&
                    (lstFirstPlayable.Count > 1)))
                {
                    return false;
                }
            }
            return true;
            int nFirstIndex =  lstFirstPlayable.IndexOf(first);
            /*int nFirstPriority = 
                    (nFirstIndex * 10) +
                        ((nFirstIndex == lstFirstPlayable.Count) ?
                        (0) :
                        (Math.Abs(lstFirstPlayable[nFirstIndex].Priority - first.Priority)));*/

            int nToPlayIndex = lstToPlay.IndexOf(first);
          /*  int nToPlayPriority =
                       (nToPlayIndex * 10) +
                           ((nToPlayIndex == lstToPlay.Count) ?
                           (0) :
                           (Math.Abs(lstToPlay[nToPlayIndex].Priority - cToRemove.Priority)));
            */
            if (nFirstIndex >= nToPlayIndex)
            {
                return true;
            }
            return false;
        }

        private bool GetPosToRemove<TFstFinger,TSecFinger>(List<CGuitarFinger> lstFirstRemove, List<CGuitarFinger> lstSecondToRemove, CGuitarFinger cCurrFirstPos, CGuitarFinger cCurrSecondPos)
        {
            if (CanBePlayedOnOpen(lstFirstRemove, cCurrFirstPos))
            {
                AddToListIfDontExist(lstFirstRemove, GetNewFinger(typeof(TFstFinger), cCurrFirstPos));
                return false;
            }

            if (cCurrFirstPos.Strings.Count == 1)
            {
                if (cCurrSecondPos.Strings.Count > 1)
                {
                    if(cCurrSecondPos.Strings.Contains(cCurrFirstPos.Strings[0]))
                    {
                        AddToListIfDontExist(lstSecondToRemove, GetNewFinger(typeof(TSecFinger), cCurrSecondPos));
                        return false;
                    }
                }
            }
         /*   else if (cCurrSecondPos.Strings.Count == 1)
            {
                if (cCurrFirstPos.Strings.Contains(cCurrSecondPos.Strings[0]))
                {
                    AddToListIfDontExist(lstFirstRemove, GetNewFinger(typeof(TFstFinger),cCurrFirstPos));
                    return false;
                }
            }*/
            else
            {
                bool fMatchingStrings = true;

                CGuitarFinger lstLarger = (cCurrFirstPos.Strings.Count < cCurrSecondPos.Strings.Count) ? (cCurrSecondPos) : (cCurrFirstPos);
                CGuitarFinger lstSmaller = (cCurrFirstPos.Strings.Count < cCurrSecondPos.Strings.Count) ? (cCurrFirstPos) : (cCurrSecondPos);

                foreach (EString eCurrString in lstSmaller.Strings)
                {
                    if (!lstLarger.Strings.Contains(eCurrString))
                    {
                        fMatchingStrings = false;
                        break;
                    }
                }

                if (fMatchingStrings)
                {
                    return true;
                }
            }

            if (cCurrSecondPos.Fret > cCurrFirstPos.Fret)
            {
                AddToListIfDontExist(lstFirstRemove, GetNewFinger(typeof(TFstFinger), cCurrFirstPos));
            }
          /*  else if (cCurrSecondPos.Fret < cCurrFirstPos.Fret)
            {

                AddToListIfDontExist(lstSecondToRemove, GetNewFinger(typeof(TSecFinger), cCurrSecondPos));
            }*/
            else if(cCurrSecondPos.Fret == cCurrFirstPos.Fret)
            {
                EString eMinString = GetMinString(cCurrSecondPos.Strings);
                EString eMinStringToCompare = GetMinString(cCurrFirstPos.Strings);

                if (eMinString < eMinStringToCompare)
                {
                    AddToListIfDontExist(lstFirstRemove, GetNewFinger(typeof(TFstFinger),cCurrFirstPos));
                }
               /* else if (eMinString > eMinStringToCompare)
                {
                    AddToListIfDontExist(lstSecondToRemove, GetNewFinger(typeof(TSecFinger), cCurrSecondPos));
                }*/
                else if(eMinString == eMinStringToCompare)
                {
                    // AddToListIfDontExist(lstFirstRemove,    GetNewFinger(typeof(TFstFinger),cCurrFirstPos));
                    // AddToListIfDontExist(lstSecondToRemove, GetNewFinger(typeof(TSecFinger), cCurrSecondPos));
                    return true;
                }
            }

            return false;
        }

        private bool CanBePlayedOnOpen(List<CGuitarFinger> lstFirstRemove, CGuitarFinger cCurrFirstPos)
        {
            // Check if can be replaced with empty strings.
            foreach (EString eCurrString in cCurrFirstPos.Strings)
            {
                if (!OpenStrings.Contains(eCurrString))
                {
                    return false;
                }
            }

            return true;
        }

        private static void AddToListIfDontExist<T>(List<T> lst, List<T> lstObj)
        {
            foreach (T cObj in lstObj)
            {
                AddToListIfDontExist(lst, cObj);
            }
        }
        private static void AddToListIfDontExist<T>(List<T> lst, T obj)
        {
            if (lst.Contains(obj))
            {
                return;
            }

            lst.Add(obj);
        }

        private EString GetMinString(List<EString> lstStrings)
        {
            if (lstStrings == null)
            {
                throw new Exception();
            }

            EString eResult = lstStrings[0];

            foreach (EString eCurrentString in lstStrings)
            {
                if (eResult > eCurrentString)
                {
                    eResult = eCurrentString;
                }
            }

            return eResult;
        }

        private List<CGuitarFinger> BuildAllOptionsFromFinger(CGuitarFinger cFinger)
        {
            if (cFinger == null || !cFinger.IsValid())
            {
                return new List<CGuitarFinger>();
            }

            return BuildAllOptionsFromFinger(cFinger, null, cFinger.Strings.Count);
        }

        private List<CGuitarFinger> BuildAllOptionsFromFinger(CGuitarFinger cFinger, List<CGuitarFinger> lstAllOptions, int nSize)
        {
            if (cFinger == null || !cFinger.IsValid())
            {
                return new List<CGuitarFinger>();
            }

            if (nSize > cFinger.Strings.Count)
            {
                throw new Exception();
            }

            List<CGuitarFinger> lstResult;
            if (lstAllOptions == null)
            {
                lstResult = new List<CGuitarFinger>();
            }
            else
            {
                lstResult = new List<CGuitarFinger>(lstAllOptions);
            }

            if (nSize == 0)
            {
                return lstResult;
            }

            AddToListIfDontExist(lstResult, BuildAllOptionsFromFinger(cFinger, lstResult, nSize - 1));

            for (int i = 0; i < cFinger.Strings.Count; i++)
            {
                CGuitarFinger cTemp = GetNewFinger(cFinger.GetType());
                for (int j = 0; j < nSize; j++)
                {
                    cTemp.Add(new CFingerPosition(cFinger.Strings[j % cFinger.Strings.Count], cFinger.Fret));
                }

                AddToListIfDontExist(lstResult, GetNewFinger(cTemp.GetType(), cTemp));
            }

            return lstResult;
        }

        private List<CGuitarFinger> FingerCanPlay(CGuitarFinger cToCheck, CGuitarFinger pTempFirst, CGuitarFinger pTempSecond, CGuitarFinger pTempThird, CGuitarFinger pTempFourth)
        {
            List<CGuitarFinger> result = new List<CGuitarFinger>();

            if (cToCheck == null)
            {
                throw new Exception();
            }

            AddAllOptionsForFinger(cToCheck, pTempFirst, result);
            AddAllOptionsForFinger(cToCheck, pTempSecond, result);
            AddAllOptionsForFinger(cToCheck, pTempThird, result);
            AddAllOptionsForFinger(cToCheck, pTempFourth, result);

            return result;
        }

        private void AddAllOptionsForFinger(CGuitarFinger cToCheck, CGuitarFinger cFinger, List<CGuitarFinger> result)
        {
            List<CGuitarFinger> lstAllOptions = new List<CGuitarFinger>(BuildAllOptionsFromFinger(cFinger));
            foreach (CGuitarFinger currMove in lstAllOptions)
            {
                if (cToCheck.CanPlay(currMove))
                {
                    AddPosToList(result, GetNewFinger(cToCheck.GetType(), cFinger));
                }
            }
        }

        private void AddPosToList(List<CGuitarFinger> lstToCheck, CGuitarFinger cToCheck)
        {
            bool fCanBeReplaced = true;

            CGuitarFinger cBigger = null;
            CGuitarFinger cSmaller = null;
            AddToListIfDontExist(lstToCheck, cToCheck);
            foreach (CGuitarFinger cCurrPos in lstToCheck)
            {
                if (cCurrPos.Fret != cToCheck.Fret)
                {
                    continue;
                }

                if (cCurrPos.Strings.Count < cToCheck.Strings.Count)
                {
                    cBigger =  cToCheck;
                    cSmaller = cCurrPos;
                }
                else
                {
                    cBigger = cCurrPos;
                    cSmaller = cToCheck;
                }
                
                // Go through all the strings.
                foreach (EString eCurrString in cSmaller.Strings)
                {
                    if (!cBigger.Strings.Contains(eCurrString))
                    {
                        continue;
                    }

                    fCanBeReplaced = false;
                    break;
                }
            }

            if (fCanBeReplaced)
            {
                if (!lstToCheck.Remove(cSmaller))
                {
                    throw new Exception();
                }
            }
        }
    };
}
